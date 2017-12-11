package com.mweeksconsulting.lanwarapp.staff.data_handler

import android.arch.lifecycle.LiveData
import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.storage.FirebaseStorage
import com.mweeksconsulting.lanwarapp.*
import com.mweeksconsulting.lanwarapp.LanWarApplication.appSingleton.context
import com.mweeksconsulting.lanwarapp.sponsor.data.*
import com.mweeksconsulting.lanwarapp.staff.Staff
import com.mweeksconsulting.lanwarapp.staff.StaffConstants
import java.util.concurrent.Executors


/**
 * Created by michael on 17/10/17.
 * handle data operations
 */
class StaffRepo:Runnable {

    override fun run() {
        Log.i("staff repo", "sponsor repo start init")
        refreshStaffArray()
        Log.i("staff repo", "sponsor repo finished init")    }

    //returns the local DB first
    //while refresh sponsors runs in the background
    fun getStaff(): LiveData<List<Staff>>? {
        Log.i("staff repo", "get staff repo from staff DAO")
        return  LoadStaffFromDB().execute().get()
    }


    private fun refreshStaffArray() {
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
            val localDateArr :Array<String>?= LoadStaffFromDB.LoadStaffDate().execute().get()
            updateStaff(localDateArr,remoteInstance)
        }

        Log.i("staff repo", "after update staff")
    }

    /**
     * Check and see if a download should start
     */


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
                            if ( localDateArr == null  ||
                                    (localDateArr.isEmpty()||localDateArr.size>1) ||
                                    cloudDate != localDateArr[0]){
                                Log.i("staff repo", "must download files")

                                staffPageRef.stream.addOnSuccessListener { snapShot ->
                                    Executors.newSingleThreadExecutor().execute(DownloadStaffData(snapShot.stream,
                                            cloudDate, storageRef,
                                            context.filesDir.path + "/" + StaffConstants().STAFF_IMAGE_LOCATION,
                                            storageStaffImageLocation))
                                }
                            }
                        }
    }
}



