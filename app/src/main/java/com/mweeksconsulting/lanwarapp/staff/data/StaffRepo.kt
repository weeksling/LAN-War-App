package com.mweeksconsulting.lanwarapp.sponsor.data

import android.arch.lifecycle.LiveData
import android.util.Log
import javax.inject.Singleton
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.storage.FirebaseStorage
import com.mweeksconsulting.lanwarapp.*
import com.mweeksconsulting.lanwarapp.LanWarApplication.appSingleton.context
import com.mweeksconsulting.lanwarapp.sponsor.*
import java.util.concurrent.Executors


/**
 * Created by michael on 17/10/17.
 * handle data operations
 */

@Singleton
class StaffRepo{
    val staffDAO : StaffDAO

    init {
        staffDAO = LanWarApplication.appSingleton.staffDAO
        Log.i("sponsor Repo", "sponsor repo start init")
        refreshSponsorArray()
        Log.i("sponsor Repo", "sponsor repo finished init")
    }

    private fun refreshSponsorArray() {
        val localDateArr :Array<String>?= LoadStaffFromDB.LoadSponsorsDate().execute().get()
        updateSponsors(localDateArr)
        Log.i("sponsor Repo", "after update sponsors")
    }


    //returns the local DB first
    //while refresh sponsors runs in the background
    fun getSponsors(): LiveData<List<Sponsor>>? {
        refreshSponsorArray()
        Log.i("sponsor Repo", "get sponsors from sponsor DAO")
        return  LoadStaffFromDB().execute().get()
    }


    /**
     * Check and see if a download should start
     */
    private fun updateSponsors(localDateArr :  Array<String>?){

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

        //fetch the remote config values off the cloud
        remoteInstance.fetch(cacheExpiration)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        remoteInstance.activateFetched()
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
                                    Executors.newSingleThreadExecutor().execute(DownloadStaffData(snapShot.stream, cloudDate, storageRef, staffDAO, context.filesDir.path + "/" + SponsorConstants().SPONSOR_IMAGE_LOCATION))
                                }
                            }
                        }
                    }
                }
    }









}




