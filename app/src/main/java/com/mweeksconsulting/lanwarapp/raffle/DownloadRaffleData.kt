package com.mweeksconsulting.lanwarapp.raffle

import android.util.Log
import com.google.firebase.storage.StorageReference
import com.mweeksconsulting.lanwarapp.sponsor.Sponsor
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import java.io.InputStream
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Created by michael on 10/11/17.
 * This class downloads the raffle data that was stored on Firebase
 */

class DownloadRaffleData(val inStream:InputStream, val cloudDate :String, val raffleImageRef :StorageReference, val imageFilePath:String) :Runnable{

    override fun run() {
        //set thread to background
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            Log.i("Download raffle,", Thread.currentThread().name)
        LoadRafflesFromDB.DeleteRaffles().execute().get()

        //normalize the xml stream
        val docFactory = DocumentBuilderFactory.newInstance()
            val docBuilder = docFactory.newDocumentBuilder()
            val doc = docBuilder.parse(inStream)
            doc.documentElement.normalize()

        val raffleList = doc.getElementsByTagName("raffle")

        //build and store each raffle object
        for (i in 0 until raffleList.length) {
                val node = raffleList.item(i)
                if (node.nodeType == Node.ELEMENT_NODE) {
                    val attritubeNodes = node as Element

                    var date = attritubeNodes.getElementsByTagName("date")?.item(0)?.textContent
                    var time = attritubeNodes.getElementsByTagName("time")?.item(0)?.textContent
                    var location = attritubeNodes.getElementsByTagName("location")?.item(0)?.textContent
                    val itemList = attritubeNodes.getElementsByTagName("item")
                    if (date == null) {
                        date = "????"
                    }
                    if (time == null) {
                        time = "????"
                    }
                    if (location == null) {
                        location = "????"
                    }

                    //raffle information found store to DB
                    //must insert before items due to the items
                    //foriegn key constraint
                    val raffle = Raffle(date, time, location, cloudDate)
                    raffle.id = LoadRafflesFromDB.InsertRaffle(raffle).execute().get()

                    //get the items asociated to the raffle and store them
                    for (i in 0 until itemList.length) {
                        val itemNode = itemList.item(i)
                        if (itemNode.nodeType == Node.ELEMENT_NODE) {
                            val attritubeNodes = itemNode as Element
                            var title = attritubeNodes.getElementsByTagName("title")?.item(0)?.textContent
                            val img_name = attritubeNodes.getElementsByTagName("img_name")?.item(0)?.textContent
                            if (title == null) {
                                title = ""
                            }
                            var imgFile: File? = null
                            val dir = File(imageFilePath)
                            dir.mkdirs()
                            if (img_name != null) {
                                imgFile = File(dir, img_name)
                            }
                            val item = Item(raffle.id, title, imgFile?.path)
                            Log.i("download raffle", "item saved $item")

                            LoadRafflesFromDB.SaveItem(item).execute().get()
                            //Download item images
                            if (img_name != null   && img_name.isNotEmpty()   && imgFile != null) {
                                Log.i("download raffle", "image path: " + imageFilePath)
                                Log.i("download raffle", "image name: " + img_name)
                                val imgRef = raffleImageRef.child(img_name)
                                Log.i("download raffle", "image file pth: " + imgFile.path)
                                Log.i("download raffle", "image ref path: " + imgRef.path)

                                imgRef.getFile(imgFile).addOnSuccessListener {
                                    Log.i("download raffle", "image name" + imgFile.name)
                                }.removeOnFailureListener {
                                    Log.i("download raffle", "FAILED")
                                }
                            }
                        }
                    }
                }
            }
    }
}