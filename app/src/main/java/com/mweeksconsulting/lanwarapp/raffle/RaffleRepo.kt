package com.mweeksconsulting.lanwarapp.raffle

import android.arch.lifecycle.LiveData
import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.storage.FirebaseStorage
import com.mweeksconsulting.lanwarapp.BuildConfig
import com.mweeksconsulting.lanwarapp.LanWarApplication
import com.mweeksconsulting.lanwarapp.sponsor.Sponsor
import java.util.concurrent.Executors

/**
 * Created by michael on 12/12/17.
 */
class RaffleRepo:Runnable {
   override fun run() {
       refreshRaffleArray()
    }

    //while refresh sponsors runs in the background
    fun getRaffles():  LiveData<List<Raffle>>?{
        Log.i("raffle repo", "get raffle repo from raffle DAO")
        Executors.newSingleThreadExecutor().execute(this)
        return  LoadRafflesFromDB().execute().get()
    }

    private fun refreshRaffleArray() {
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
            val createDateArr :Array<String>?= LoadRafflesFromDB.LoadCreateDates().execute().get()
            updateRaffle(createDateArr,remoteInstance)
        }

        Log.i("raffle repo", "after update raffle")
    }

    /**
     * Check and see if a download should start
     */
    private fun updateRaffle(localDateArr :  Array<String>?,remoteInstance: FirebaseRemoteConfig){

        Log.i("raffle repo","update raffle")

        val storageRaffleOrderLocation: String = remoteInstance.getString(RaffleConstants().RAFFLE_ORDER_LOCATION)
        val storageRaffleImageLocation: String = remoteInstance.getString(RaffleConstants().RAFFLE_CLOUD_IMAGE_LOCATION)
        val storageRef = FirebaseStorage.getInstance().reference
        Log.i("raffle repo",storageRaffleOrderLocation)
        val rafflePageRef = storageRef.child(storageRaffleOrderLocation)
        val raffleImageRef = storageRef.child(storageRaffleImageLocation)

        rafflePageRef.metadata.addOnSuccessListener { storageMetadata ->
            val cloudDate = storageMetadata.updatedTimeMillis.toString()

            //if local dates array is empty or more than 1 update
            //or if the cloud date != localdate
            if ( localDateArr == null  ||
                    (localDateArr.isEmpty()||localDateArr.size>1) ||
                    cloudDate != localDateArr[0]){
                Log.i("raffle repo", "must download files")

                rafflePageRef.stream.addOnSuccessListener { snapShot ->
                    val imgFilePath =  LanWarApplication.appSingleton.context.filesDir.path + "/" + RaffleConstants().RAFFLE_IMAGE_LOCATION
                    val dlRaffleData = DownloadRaffleData(snapShot.stream,cloudDate,raffleImageRef,imgFilePath)
                    Executors.newSingleThreadExecutor().execute(dlRaffleData)
                }
            }
        }
    }
}