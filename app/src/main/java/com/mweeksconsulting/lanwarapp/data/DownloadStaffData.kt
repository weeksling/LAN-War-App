package com.mweeksconsulting.lanwarapp.sponsor.data

import android.util.Log
import com.google.firebase.storage.StorageReference
import com.mweeksconsulting.lanwarapp.R.id.sponsorList
import com.mweeksconsulting.lanwarapp.sponsor.Sponsor
import com.mweeksconsulting.lanwarapp.staff.Staff
import com.mweeksconsulting.lanwarapp.staff.StaffConstants
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import java.io.InputStream
import java.io.Serializable
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Created by michael on 10/11/17.
 */

class DownloadStaffData(val inStream:InputStream, val cloudDate :String, val storageRef:StorageReference, val imageFilePath:String, val storageImageFolder:String) :Runnable{

    override fun run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND)
            Log.i("Download staff,", Thread.currentThread().name)

            val docFactory = DocumentBuilderFactory.newInstance()
            val docBuilder = docFactory.newDocumentBuilder()
            val doc = docBuilder.parse(inStream)
            doc.documentElement.normalize()

            val staffList = doc.getElementsByTagName("staff")
            val newStaffArray = arrayListOf<Staff>()
            for (i in 0 until staffList.length) {

                val node = staffList.item(i)
                if (node.nodeType == Node.ELEMENT_NODE) {
                    val attritubeNodes = node as Element

                    var name = attritubeNodes.getElementsByTagName("staff_name")?.item(0)?.textContent
                    val img_name = attritubeNodes.getElementsByTagName("img_name")?.item(0)?.textContent
                    var alias = attritubeNodes.getElementsByTagName("alias")?.item(0)?.textContent
                    var role = attritubeNodes.getElementsByTagName("role")?.item(0)?.textContent

                    if (name == null) {
                        name = "NA"
                    }
                    if (alias == null) {
                        alias = "NA"
                    }
                    if (role == null) {
                        role = "NA"
                    }

                    val dir = File(imageFilePath)
                    dir.mkdirs()
                    val imgFile = File(dir, img_name)
                    Log.i("download staff","image name: " + img_name)


                    Log.i("download staff",storageImageFolder)
                    if (img_name!=null && img_name.isNotEmpty()) {

                        val imageFolder = storageRef.child(storageImageFolder)

                        val imgRef = imageFolder.child(img_name)
                        Log.i("download staff", "image file pth: " + imgFile.path)
                        Log.i("download staff", "image ref path: " + imgRef.path)
                        Log.i("download staff", "image file is a file")
                        //if the file does not already exist download it
                        //make sure file doesn't already exist before a download
                        imgRef.getFile(imgFile).addOnSuccessListener {
                            Log.i("download staff", "image name" + imgFile.name)
                        }.removeOnFailureListener {
                            Log.i("download staff", "FAILED")
                        }
                    }else{
                        Log.i("download staff", "cannot download staff image")
                        Log.i("download staff", "imge file existts?: "+imgFile.exists())
                        Log.i("download staff", "imge file name?: "+img_name)
                        Log.i("download staff", "imge file path?: "+imgFile.path)



                    }
                    val staff = Staff(name, alias, role, imgFile.path, cloudDate)
                    newStaffArray.add(staff)
                }
            }

                newStaffArray.forEach {
                    s ->   Log.i("download staff",s.toString())
                }
                Log.i("download staff","saved to DB")
                LoadStaffFromDB.DeleteStaff().execute().get()
                LoadStaffFromDB.InsertStaff(newStaffArray).execute()
    }
}