package com.mweeksconsulting.lanwarapp.sponsor

import android.util.Log
import java.io.File

/**
 * Created by michael on 31/10/17.
 * This class updates the list view for sponsors
 * useLocalSponsorData gets the local file
 * useCloudData updaed the app with the cloud data if it has been updaded
 */
class SponsorInformation (val observer: SponsorObserver){
    private val SPONSOR_ORDER_LOCATION = "sponsor_order_location"
    private val UPDATE_DATE = "update_date"


    //gets local sponsor information
    fun useLocalSponsorData(){
        val sponsorFile = File(observer.context.filesDir.path + "/" + "sponsorOrder.xml")
        val img_dir = File(observer.context.filesDir.path + "/sponsor_images/")

        //read xml file in background
        var res = false
        //res = sponsorFile.delete()
        Log.i("delete Sponsor File", res.toString())

        val readXmlFile = ReadSponsorXMLFileInBackground(observer,img_dir)
        readXmlFile.execute(sponsorFile)
        Log.i("Sponsor Information","use local sponsor")
    }


    /*
    get cloud information
     */
    fun useCloudData() {
        val sponsor_img_Dir = File(observer.context.filesDir.path + "/sponsor_images")

        val downloadInBackground = DownloadSponsorFilesInBackground(SPONSOR_ORDER_LOCATION,UPDATE_DATE,sponsor_img_Dir,observer)
        downloadInBackground.execute()
        Log.i("Sponsor Information","use cloud sponsor")

    }



}