package com.mweeksconsulting.lanwarapp.staff

import android.util.Log
import java.io.File

/**
 * Created by michael on 31/10/17.
 * Downloads all the sponsor files in an async task from google firebase
 * compares the update date of the xml file downloaded and the cloud meta date update date
 */
class StaffInformation(val observer: StaffObserver){
    private val STAFF_ORDER_LOCATION = "STAFF_ORDER_LOCATION"
    private val LOCAL_STAFF_IMG_PATH = "STAFF_IMG_LOCATION"
    private val UPDATE_DATE = "UPDATE_DATE"



    fun useLocalStaffData(){

        val staffFile = File(observer.context.filesDir.path + "/" + "staffOrder.xml")
        val deleteThis = File(observer.context.filesDir.path,"staffOrder.xml")
        var res = false
        //res = deleteThis.delete()
        Log.i("delete Staff File", res.toString())

        //read xml file in background
        val staffImgPath= observer.context.filesDir.path+"/staff_pictures/"

        val readXmlFile = ReadStaffFileInBackground(STAFF_ORDER_LOCATION,staffImgPath,observer)
        readXmlFile.execute(staffFile)
        Log.i("Staff Information","use local sponsor\"")
    }


    /*
    get cloud information
     */
    fun useStaffCloudData() {
        val staffFile = File(observer.context.filesDir.path + "/" + "staffOrder.xml")
        val staffImgPath= observer.context.filesDir.path+"/staff_pictures/"
        Log.i("Staff Img Path",staffFile.path)

        val downloadInBackground = downloadStaffFilesInBackground(STAFF_ORDER_LOCATION,staffImgPath,UPDATE_DATE,observer)
        downloadInBackground.execute(staffFile)
        Log.i("Staff","use cloud staff")

    }

}