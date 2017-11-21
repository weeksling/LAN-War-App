package com.mweeksconsulting.lanwarapp.sponsor.data

import android.util.Log
import com.google.firebase.storage.StorageReference
import com.mweeksconsulting.lanwarapp.sponsor.Sponsor
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Created by michael on 10/11/17.
 */

class DownloadSponsorData(val inStream:InputStream, val cloudDate :String, val sponsorPageRef:StorageReference, val imageFilePath:String) :Runnable{

    override fun run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            Log.i("Download sponsors,", Thread.currentThread().name)

            val docFactory = DocumentBuilderFactory.newInstance()
            val docBuilder = docFactory.newDocumentBuilder()
            val doc = docBuilder.parse(inStream)
            doc.documentElement.normalize()

            val sponsorList = doc.getElementsByTagName("sponsor")
            val newSponsorArray = arrayListOf<Sponsor>()
            for (i in 0 until sponsorList.length) {

                val node = sponsorList.item(i)
                if (node.nodeType == Node.ELEMENT_NODE) {
                    val attritubeNodes = node as Element
                    val name = attritubeNodes.getElementsByTagName("sponsor_name").item(0).textContent
                    val description = attritubeNodes.getElementsByTagName("description").item(0).textContent
                    val img_name = attritubeNodes.getElementsByTagName("img_name").item(0).textContent
                    val website = attritubeNodes.getElementsByTagName("website")?.item(0)?.textContent

                    val dir = File(imageFilePath)
                    dir.mkdirs()
                    val imgFile = File(dir, img_name)

                    Log.i("download sponsors", "image path: " + imageFilePath)
                    Log.i("download sponsors", "image name: " + img_name)

                    val imageFolder = sponsorPageRef.child("sponsor_logos")
                    val imgRef = imageFolder.child(img_name)

                    Log.i("download sponsors", "image file pth: " + imgFile.path)
                    Log.i("download sponsors", "image ref path: " + imgRef.path)

                        Log.i("download sponsors", "image file is a file")

                        //if the file does not already exist download it
                        //make sure file doesn't already exist before a download
                        imgRef.getFile(imgFile).addOnSuccessListener {
                            Log.i("download sponsors","image name" + imgFile.name)
                        }.removeOnFailureListener {
                            Log.i("download sponsors", "FAILED")
                        }
                    val sponsor = Sponsor(name, description, imgFile.path, cloudDate,website)
                    newSponsorArray.add(sponsor)
                    Log.i("download sponsors","saved to DB")
                }
            }

        newSponsorArray.forEach {
            s ->                     Log.i("download sponsors",s.toString())

        }
        LoadSponsorsFromDB.DeleteSponsors().execute().get()
        LoadSponsorsFromDB.InsertSponsors(newSponsorArray).execute()
    }
}