package com.mweeksconsulting.lanwarapp.Sponsor_Package

import android.os.AsyncTask
import android.os.Bundle
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Created by michael on 16/10/17.
 */
class readSponsorXMLFileInBackground:AsyncTask<File,Void,Bundle>() {
    lateinit var observer: SponsorObserver
    private val UPDATE_DATE = "update_date"
    private val SPONSOR_ORDER_LOCATION = "sponsor_order_location"



    override fun doInBackground(vararg sponsorFileArr: File): Bundle {

        val updateDate:Long
        val storageLocation:String
        val sponsorArray = ArrayList<Sponsor>()

        if (sponsorFileArr[0].exists()) {
            val dbFactory = DocumentBuilderFactory.newInstance()
            val docBuilder = dbFactory.newDocumentBuilder()
            val doc = docBuilder.parse(sponsorFileArr[0])
            doc.documentElement.normalize()

            val root = doc.documentElement
            storageLocation = root.getAttribute(SPONSOR_ORDER_LOCATION)
            updateDate =  root.getAttribute(UPDATE_DATE).toLong()

            //sponsor list
            val sponsorList = doc.getElementsByTagName("sponsor")
            for (i in 0 until sponsorList.length){
                val node = sponsorList.item(i)
                if (node.nodeType == Node.ELEMENT_NODE) {
                    val element :Element = node as Element
                    val name = element.getElementsByTagName("sponsor_name").item(0).textContent
                    val img_name = element.getElementsByTagName("img_name").item(0).textContent
                    val description = element.getElementsByTagName("description").item(0).textContent
                    println(img_name)
                    val img_path = observer.context.filesDir.path+"/sponsor_images/" +img_name
                    println(img_path)
                    val sponsor = Sponsor(name, description, img_path, observer.context)
                    sponsorArray.add(sponsor)
                }
            }

            val img_dir = File(observer.context.filesDir.path+"/sponsor_images")
            val fileList = img_dir.listFiles()


            //loop through file list
            //check if sponsor list contains file
            //if it does not contain the file delete
            val imageFiles = ArrayList<File>()
            sponsorArray.forEach {
                s->
            imageFiles.add(File(s.imagePath))
            }

            fileList.forEach {
                file ->
                if (!imageFiles.contains(file)){
                    file.delete()
                }
            }
        } else {
            updateDate = 0
            storageLocation = "THERE IS NO LOCAL FILE"
        }


        //return a bundle instead of a remote instance
        val bundle = Bundle()
        bundle.putString (SPONSOR_ORDER_LOCATION, storageLocation)
        bundle.putLong(UPDATE_DATE, updateDate)
        bundle.putSerializable("sponsors",sponsorArray)

        return bundle
    }

    override fun onPostExecute(result: Bundle) {
        super.onPostExecute(result)
        observer.setDefaultRI(result)
    }
}