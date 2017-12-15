package com.mweeksconsulting.lanwarapp.sponsor.data_handler

import android.app.IntentService
import android.arch.lifecycle.LiveData
import android.content.Intent
import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.storage.FirebaseStorage
import com.mweeksconsulting.lanwarapp.*
import com.mweeksconsulting.lanwarapp.LanWarApplication.appSingleton.context
import com.mweeksconsulting.lanwarapp.sponsor.Sponsor
import com.mweeksconsulting.lanwarapp.sponsor.SponsorConstants
import com.mweeksconsulting.lanwarapp.sponsor.data.*
import com.mweeksconsulting.lanwarapp.staff.Staff
import com.mweeksconsulting.lanwarapp.staff.StaffConstants
import java.util.concurrent.Executors


/**
 * Created by michael on 17/10/17.
 * handle data operations
 */

class SponsorRepo:Runnable {

    override fun run() {
        refreshSponsorArray()
        Log.i("sponsor SponsorRepo", "sponsor repo start init")
        Log.i("sponsor SponsorRepo", "sponsor repo finished init")
    }


    fun getSponsors(): LiveData<List<Sponsor>>? {
        Log.i("sponsor SponsorRepo", "get sponsors from sponsor DAO")
        return  LoadSponsorsFromDB().execute().get()
    }

    private fun refreshSponsorArray() {
        val remoteInstance = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
        remoteInstance.setConfigSettings(configSettings)
        //fetch remote instance values
        var cacheExpiration: Long = 3600
        if (remoteInstance.info.configSettings.isDeveloperModeEnabled) {
            cacheExpiration = 0
        }

        remoteInstance.fetch(cacheExpiration).addOnSuccessListener {
            remoteInstance.activateFetched()
            val localDateArr :Array<String>?= LoadSponsorsFromDB.LoadSponsorsDate().execute().get()
            updateSponsor(localDateArr,remoteInstance)
            Log.i("sponsor SponsorRepo", "after update sponsors")
        }
    }


    /**
     * Check and see if a download should start
     */


    private fun updateSponsor(localDateArr :  Array<String>?,remoteInstance:FirebaseRemoteConfig){

        val storageStaffOrderLocation: String = remoteInstance.getString(SponsorConstants().SPONSOR_ORDER_LOCATION)
        val storageRef = FirebaseStorage.getInstance().reference
        Log.i("REPO",storageStaffOrderLocation)
        val sponsorPageRef = storageRef.child(storageStaffOrderLocation)

        sponsorPageRef.metadata.addOnSuccessListener { storageMetadata ->
            val cloudDate = storageMetadata.updatedTimeMillis.toString()
            //if local dates array is empty or more than 1 update
            //or if the cloud date != localdate
            if ( localDateArr == null ||
                    (localDateArr.isEmpty()||localDateArr.size>1) ||
                    cloudDate != localDateArr[0]){
                Log.i("Sponsor repo", "must download files")
                Log.i("Sponsor repo",   localDateArr?.size.toString())
//                 Log.i("Sponsor repo",   localDateArr?.get(0))
                Log.i("Sponsor repo",   cloudDate)


                sponsorPageRef.stream.addOnSuccessListener { snapShot ->
                    Executors.newSingleThreadExecutor().execute(DownloadSponsorData(snapShot.stream,
                            cloudDate, storageRef,
                            context.filesDir.path + "/" + SponsorConstants().SPONSOR_IMAGE_LOCATION))
                }
            }
        }
    }

}



