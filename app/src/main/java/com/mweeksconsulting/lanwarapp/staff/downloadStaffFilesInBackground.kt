package com.mweeksconsulting.lanwarapp.staff

import android.os.AsyncTask
import android.os.Debug
import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.storage.FirebaseStorage
import com.mweeksconsulting.lanwarapp.BuildConfig
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * Created by michael on 17/10/17.
 * Download staff files in the background using async task
 * uses firebase to store files (img and xml layout file)
 * also uses remote config to help ensure paths match in the future
 */
class downloadStaffFilesInBackground (val STAFF_ORDER_LOCATION: String, val LOCAL_STAFF_IMG_PATH:String,
                                      val UPDATE_DATE:String, val  observer: StaffObserver )
    : AsyncTask<File, ArrayList<Staff>, ArrayList<Staff>>() {
    val staffArray = ArrayList<Staff>()

    //get staff online
    override fun doInBackground(vararg p0: File): ArrayList<Staff> {

        var localStorageLocation = "NO FILE"
        var localDate = "NO FILE"
        val staffFile = p0[0]
        if (staffFile.exists()) {
            //get the root values
            val docFactory = DocumentBuilderFactory.newInstance()
            val docBuilder = docFactory.newDocumentBuilder()
            val doc = docBuilder.parse(staffFile)
            doc.documentElement.normalize()

            val rootElement = doc.documentElement
            localStorageLocation = rootElement.getAttribute(STAFF_ORDER_LOCATION)
            localDate = rootElement.getAttribute(UPDATE_DATE)
        }

            val remoteInstance = FirebaseRemoteConfig.getInstance()
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                    .setDeveloperModeEnabled(BuildConfig.DEBUG)
                    .build()
            remoteInstance.setConfigSettings(configSettings)

            val default: MutableMap<String, Any> = hashMapOf()
            default.put(STAFF_ORDER_LOCATION, localStorageLocation)
            default.put(UPDATE_DATE, localDate)
            remoteInstance.setDefaults(default)


            var cacheExpiration: Long = 3600
            if (remoteInstance.info.configSettings.isDeveloperModeEnabled) {
                cacheExpiration = 0
            }


            println("Cache expiration date: " + cacheExpiration)
            remoteInstance.fetch(cacheExpiration)
                    .addOnCompleteListener { task ->
                        Log.i("Staff File Download", "Remote Instance Fetch Complete")
                        //if the fetch was good then get the download uri
                        if (task.isSuccessful) {

                            remoteInstance.activateFetched()

                            //storage reference
                            val storageStringLocation: String = remoteInstance.getString(STAFF_ORDER_LOCATION)
                            val storageRef = FirebaseStorage.getInstance().reference
                            val staffPageRef = storageRef.child(storageStringLocation)

                                //also wish I knew this early on
                            staffPageRef.metadata.addOnSuccessListener { storageMetadata ->
                                    val cloudUpdateDate = storageMetadata.updatedTimeMillis.toString()
                                    if (cloudUpdateDate != localDate || localStorageLocation == "NO LOCAL STORAGE") {
                                        Log.i("Staff File Download", "dates do not match")


                                        staffPageRef.getFile(staffFile).addOnSuccessListener {
                                            Log.i("Staff File Download", "staff file downloaded")

                                        val storageLocation = remoteInstance.getString(STAFF_ORDER_LOCATION)
                                        val updateDate = storageMetadata.updatedTimeMillis.toString()

                                            val docFactory = DocumentBuilderFactory.newInstance()
                                            val docBuilder = docFactory.newDocumentBuilder()
                                            val doc = docBuilder.parse(staffFile)
                                            doc.documentElement.normalize()

                                            val rootElement = doc.documentElement
                                            rootElement.setAttribute(STAFF_ORDER_LOCATION, storageLocation)
                                            rootElement.setAttribute(UPDATE_DATE, updateDate)

                                            //save data to file
                                            val transformer = TransformerFactory.newInstance().newTransformer()
                                            val output = StreamResult(staffFile)
                                            val input = DOMSource(doc)
                                            transformer.transform(input, output)

                                            Log.i("Staff File Path",staffFile.path)

                                            val sponsorList = doc.getElementsByTagName("staff")
                                            for (i in 0 until sponsorList.length) {
                                                val node = sponsorList.item(i)
                                                if (node.nodeType == Node.ELEMENT_NODE) {
                                                val attritubeNodes = node as Element

                                                var name = attritubeNodes.getElementsByTagName("staff_name")?.item(0)?.textContent
                                                var img_name = attritubeNodes.getElementsByTagName("img_name")?.item(0)?.textContent
                                                var alias = attritubeNodes.getElementsByTagName("alias")?.item(0)?.textContent
                                                var role = attritubeNodes.getElementsByTagName("role")?.item(0)?.textContent

                                                if (name == null || name == "") {
                                                    name = "NA"
                                                }
                                                if (alias == null || alias == "") {
                                                    alias = "NA"
                                                }
                                                if (role == null || role == "") {
                                                    role = "NA"
                                                }
                                                if (img_name == null || img_name == "") {
                                                    img_name = "NA"
                                                }

                                                val nameText: String = name
                                                val aliasText: String = alias
                                                val roleText: String = role
                                                val img_name_text: String = img_name


                                                  //  val sponsorImageDir = File(observer.context.filesDir.parent + "/sponsor_images")
                                                   // sponsorImageDir.mkdir()


                                                //imageLocation
                                                // download this file in the background
                                                val imageFolder = storageRef.child("staff_pictures")



                                                if (img_name_text != "NA") {
                                                    val imgRef = imageFolder.child(img_name_text)
                                                    Log.i("Staff Img Name","NA")
                                                    val dir = File(LOCAL_STAFF_IMG_PATH)
                                                    dir.mkdir()
                                                    val imgFile = File(dir, img_name_text)
                                                    println(imgFile.path)
                                                    println(imgRef.path)


                                                    imgRef.getFile(imgFile).addOnSuccessListener {
                                                        Log.i("Staff Img download","success")
                                                        val staff = Staff(nameText, aliasText, roleText, imgFile.path, observer.context)
                                                        staffArray.add(staff)
                                                        publishProgress(staffArray)
                                                    }.addOnFailureListener {
                                                        Log.i("Staff Img download","fail")

                                                        val staff = Staff(nameText, aliasText, roleText, "NO CLOUD FILE", observer.context)
                                                        staffArray.add(staff)
                                                        publishProgress(staffArray)
                                                    }
                                                } else {
                                                    val staff = Staff(nameText, aliasText, roleText, "NO LOCAL FILE", observer.context)
                                                    staffArray.add(staff)
                                                    publishProgress(staffArray)
                                                }

                                        }
                                    }
                                }
                            }else{
                                        Log.i("Staff file","staff dates match")
                                    }
                        }.addOnFailureListener {
                                    Log.i("Staff file", "fail remote instance")
                                }
                    }
        }
        return staffArray
    }



    override fun onProgressUpdate(vararg values: ArrayList<Staff>) {
        super.onProgressUpdate(*values)
            observer.refreshStaff(values[0])

    }

    //could possibly return a blank if network is slow
      override fun onPostExecute(result: ArrayList<Staff>) {
        super.onPostExecute(result)
        if(staffArray.size>0) {
            observer.refreshStaff(result)
        }
    }
}