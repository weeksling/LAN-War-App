package com.mweeksconsulting.lanwarapp.data

import android.app.IntentService
import android.arch.lifecycle.LiveData
import android.content.Intent
import android.util.Log
import javax.inject.Singleton
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.storage.FirebaseStorage
import com.mweeksconsulting.lanwarapp.*
import com.mweeksconsulting.lanwarapp.LanWarApplication.appSingleton.context
import com.mweeksconsulting.lanwarapp.sponsor.*
import com.mweeksconsulting.lanwarapp.sponsor.data.*
import com.mweeksconsulting.lanwarapp.staff.Staff
import com.mweeksconsulting.lanwarapp.staff.StaffConstants
import java.util.concurrent.Executors


/**
 * Created by michael on 17/10/17.
 * handle data operations
 */

@Singleton
class Repo :IntentService("REPO"){

    init {
        //we should run these async
        Log.i("sponsor Repo", "sponsor repo start init")
        refreshData()
        Log.i("sponsor Repo", "sponsor repo finished init")
    }


    override fun onHandleIntent(p0: Intent?) {

    }

    //returns the local DB first
    //while refresh sponsors runs in the background
    fun getStaff(): LiveData<List<Staff>>? {
        Log.i("sponsor Repo", "get sponsors from sponsor DAO")
        return  LoadStaffFromDB().execute().get()
    }

    fun getSponsors(): LiveData<List<Sponsor>>? {
        Log.i("sponsor Repo", "get sponsors from sponsor DAO")
        return  LoadSponsorsFromDB().execute().get()
    }

    private fun refreshStaffArray(remoteInstance: FirebaseRemoteConfig) {
        val localDateArr :Array<String>?= LoadStaffFromDB.LoadStaffDate().execute().get()
        updateStaff(localDateArr,remoteInstance)
        Log.i("sponsor Repo", "after update sponsors")
    }

    private fun refreshSponsorArray(remoteInstance: FirebaseRemoteConfig) {
        val localDateArr :Array<String>?= LoadSponsorsFromDB.LoadSponsorsDate().execute().get()
        updateSponsors(localDateArr,remoteInstance)
        Log.i("sponsor Repo", "after update sponsors")
    }

    /**
     * Check and see if a download should start
     */
    private fun refreshData() {
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
            refreshSponsorArray(remoteInstance)
            refreshStaffArray(remoteInstance)
        }
    }


    private fun updateStaff(localDateArr :  Array<String>?,remoteInstance:FirebaseRemoteConfig){

                        val storageStaffOrderLocation: String = remoteInstance.getString(StaffConstants().STAFF_ORDER_LOCATION)
                        val storageStaffImageLocation: String = remoteInstance.getString(StaffConstants().STAFF_IMAGES_FOLDER)
                        val storageRef = FirebaseStorage.getInstance().reference
                        Log.i("REPO",storageStaffOrderLocation)
                        val staffPageRef = storageRef.child(storageStaffOrderLocation)

                        staffPageRef.metadata.addOnSuccessListener { storageMetadata ->
                            val cloudDate = storageMetadata.updatedTimeMillis.toString()

                            //if local dates array is empty or more than 1 update
                            //or if the cloud date != localdate
                            if ( (localDateArr == null ) ||
                                    (localDateArr.isEmpty()||localDateArr.size>1) ||
                                    cloudDate != localDateArr[0]){
                                Log.i("Sponsor repo", "must download files")

                                staffPageRef.stream.addOnSuccessListener { snapShot ->
                                    Executors.newSingleThreadExecutor().execute(DownloadStaffData(snapShot.stream,
                                            cloudDate, storageRef,
                                            context.filesDir.path + "/" + StaffConstants().STAFF_IMAGE_LOCATION,
                                            storageStaffImageLocation))
                                }
                            }
                        }
    }

    private fun updateSponsors(localDateArr :  Array<String>?,remoteInstance:FirebaseRemoteConfig){


                        val storageStringLocation: String = remoteInstance.getString(SponsorConstants().SPONSOR_ORDER_LOCATION)
                        val storageRef = FirebaseStorage.getInstance().reference
                        val sponsorPageRef = storageRef.child(storageStringLocation)

                        sponsorPageRef.metadata.addOnSuccessListener { storageMetadata ->
                            val cloudDate = storageMetadata.updatedTimeMillis.toString()

                            //if local dates array is empty or more than 1 update
                            //or if the cloud date != localdate
                            if ( (localDateArr == null ) ||
                                    (localDateArr.isEmpty()||localDateArr.size>1) ||
                                    cloudDate != localDateArr[0]){
                                Log.i("Sponsor repo", "must download files")

                                sponsorPageRef.stream.addOnSuccessListener { snapShot ->
                                    Executors.newSingleThreadExecutor().execute(DownloadSponsorData(snapShot.stream, cloudDate, storageRef, context.filesDir.path + "/" + SponsorConstants().SPONSOR_IMAGE_LOCATION))
                                }
                            }
                        }

                }
}



