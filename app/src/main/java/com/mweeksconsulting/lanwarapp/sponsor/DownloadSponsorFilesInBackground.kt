package com.mweeksconsulting.lanwarapp.sponsor

import android.content.Context
import android.os.AsyncTask
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
 * Download sponsorship files in the background using async task
 * uses firebase to store files (img and xml layout file)
 * also uses remote config to help ensure paths match in the future
 */
class DownloadSponsorFilesInBackground(val SPONSOR_ORDER_LOCATION:String, val UPDATE_DATE:String,val  sponsor_img_Dir: File,
                                       val observer: SponsorObserver)
                                        :AsyncTask<Void,ArrayList<Sponsor>,ArrayList<Sponsor>>() {
    val sponsorArray = ArrayList<Sponsor>()

    override fun doInBackground(vararg p0: Void?): ArrayList<Sponsor> {

        //call storage location to get the url
        //read xml file
        val sponsorOrderFile = File(observer.context.filesDir.path+"/" + "sponsorOrder.xml")
        var localStorageLocation = "NO LOCAL STORAGE"
        var localUpdateDate = "NO LOCAL UPDATE"
        if(sponsorOrderFile.exists()) {
            val dbFactory = DocumentBuilderFactory.newInstance()
            val docBuilder = dbFactory.newDocumentBuilder()
            val doc = docBuilder.parse(sponsorOrderFile)
            doc.documentElement.normalize()
            val root = doc.documentElement
             localStorageLocation = root.getAttribute(SPONSOR_ORDER_LOCATION)
             localUpdateDate = root.getAttribute(UPDATE_DATE)
        }
        //get the remote instance stuff
        val remoteInstance = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
        remoteInstance.setConfigSettings(configSettings)

        val  default:MutableMap<String,Any> = hashMapOf()
        default.put(SPONSOR_ORDER_LOCATION,localStorageLocation)
        default.put(UPDATE_DATE,localUpdateDate)
        remoteInstance.setDefaults(default)

        println("before remote instance fetched")
        var cacheExpiration: Long = 3600
        if (remoteInstance.info.configSettings.isDeveloperModeEnabled) {
            cacheExpiration = 0
        }


        //fetch the remote instance
        remoteInstance.fetch(cacheExpiration)
                .addOnCompleteListener { task ->
                    //if the fetch was good then get the download uri
                    if (task.isSuccessful) {
                        remoteInstance.activateFetched()
                        Log.i("Sponsor Download","Remote Config Fetch succesful")
                        //I want to get the online storage location and download url

                        //storage reference
                        val storageStringLocation: String = remoteInstance.getString(SPONSOR_ORDER_LOCATION)
                        val storageRef = FirebaseStorage.getInstance().reference
                        val sponsorPageRef = storageRef.child(storageStringLocation)

                        //storage url
                        //this runs in the background as well

                            sponsorPageRef.metadata.addOnSuccessListener { storageMetadata ->
                                val cloudUpdateDate = storageMetadata.updatedTimeMillis.toString()

                                Log.i("Sponsor Download","cloud update: "+cloudUpdateDate)
                                Log.i("Sponsor Download","local update: "+ localUpdateDate)
                                Log.i("Sponsor Download","local storae location: "+ localStorageLocation)

                                    if (cloudUpdateDate != localUpdateDate || localStorageLocation == "NO LOCAL STORAGE" ) {
                                        Log.i("Sponsor Download","DATES DO NOT MATCH")
                                    val storageLocation = remoteInstance.getString(SPONSOR_ORDER_LOCATION)
                                    val updateDate = storageMetadata.updatedTimeMillis.toString()

                                  //add a success listner for sponsor page ref
                                    sponsorPageRef.getFile(sponsorOrderFile).addOnSuccessListener {
                                    val docFactory = DocumentBuilderFactory.newInstance()
                                    val docBuilder = docFactory.newDocumentBuilder()
                                    val doc = docBuilder.parse(sponsorOrderFile)
                                    doc.documentElement.normalize()

                                    //set the root element in the file
                                    val rootElement = doc.documentElement
                                    rootElement.setAttribute(SPONSOR_ORDER_LOCATION, storageLocation)
                                    rootElement.setAttribute(UPDATE_DATE, updateDate)

                                    //save data to file
                                    val transformer = TransformerFactory.newInstance().newTransformer()
                                    val output = StreamResult(sponsorOrderFile)
                                    val input = DOMSource(doc)
                                    transformer.transform(input, output)

                                    val sponsorList = doc.getElementsByTagName("sponsor")
                                    for (i in 0 until sponsorList.length) {
                                        val node = sponsorList.item(i)
                                        if (node.nodeType == Node.ELEMENT_NODE) {
                                            val attritubeNodes = node as Element
                                            val name = attritubeNodes.getElementsByTagName("sponsor_name").item(0).textContent
                                            val description = attritubeNodes.getElementsByTagName("description").item(0).textContent
                                            val img_name = attritubeNodes.getElementsByTagName("img_name").item(0).textContent

                                            //imageLocation
                                            // download this file in the background
                                            val imageFolder = storageRef.child("sponsor_logos")
                                            val imgRef = imageFolder.child(img_name)

                                            sponsor_img_Dir.mkdir()
                                            val imgFile = File(sponsor_img_Dir, img_name)

                                            //make sure file doesn't already exist before a download
                                            //download file in background of async task
                                            imgRef.getFile(imgFile).addOnSuccessListener {
                                                val sponsor = Sponsor(name, description, imgFile.path, observer.context)
                                                sponsorArray.add(sponsor)
                                                publishProgress(sponsorArray)
                                            }.addOnFailureListener {
                                                val sponsor = Sponsor(name, description, imgFile.path, observer.context)
                                                sponsorArray.add(sponsor)
                                                publishProgress(sponsorArray)
                                            }
                                        }
                                    }
                                }

                            }else{
                                        Log.i("Sponsor Download","sponsor dates match ")
                                }
                        }
                    }

                }
        return sponsorArray
    }



    override fun onProgressUpdate(vararg values: ArrayList<Sponsor>) {
        super.onProgressUpdate(*values)
        Log.i("Sponsor Download","progress update")
            observer.refreshSponsors(values[0])
    }

      override fun onPostExecute(result: ArrayList<Sponsor>) {
        super.onPostExecute(result)
          if(sponsorArray.size>0) {
              Log.i("Sponsor Download","post execute")
              observer.refreshSponsors(result)
          }
      }
}