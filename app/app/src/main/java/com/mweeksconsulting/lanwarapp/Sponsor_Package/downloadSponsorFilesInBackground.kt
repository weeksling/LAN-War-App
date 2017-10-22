package com.mweeksconsulting.lanwarapp.Sponsor_Package

import android.os.AsyncTask
import com.google.firebase.storage.FirebaseStorage
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult



/**
 * Created by michael on 17/10/17.
 */
class downloadSponsorFilesInBackground:AsyncTask<Map<String,Any>,ArrayList<Sponsor>,ArrayList<Sponsor>>() {
    lateinit var observer: SponsorObserver
    private val UPDATE_DATE = "update_date"
    private  val FILE = "file"
    private val SPONSOR_ORDER_LOCATION = "sponsor_order_location"
    val sponsorArray = ArrayList<Sponsor>()



    override fun doInBackground(vararg p0: Map<String,Any>): ArrayList<Sponsor> {
        val map = p0[0]
        val date : Long = map.getValue(UPDATE_DATE) as Long
        val sponsorFile : File = map[FILE] as File
        val storageLocation: String = map[SPONSOR_ORDER_LOCATION] as String

        //call storage location to get the url

        val storageRef = FirebaseStorage.getInstance().reference
        val sponsorPageRef = storageRef.child(storageLocation)

        sponsorPageRef.getFile(sponsorFile).addOnSuccessListener {
                val docFactory = DocumentBuilderFactory.newInstance()
                val docBuilder = docFactory.newDocumentBuilder()
                val doc = docBuilder.parse(sponsorFile)
                doc.documentElement.normalize()

                val rootElement = doc.documentElement
                rootElement.setAttribute(SPONSOR_ORDER_LOCATION,storageLocation)
                rootElement.setAttribute(UPDATE_DATE,date.toString())

                //save data to file
                val transformer = TransformerFactory.newInstance().newTransformer()
                val output = StreamResult(sponsorFile)
                val input = DOMSource(doc)
                transformer.transform(input, output)

                val sponsorList = doc.getElementsByTagName("sponsor")
                for (i in 0 until sponsorList.length){
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
                        val dir = File(observer.context.filesDir.path+"/sponsor_images")
                        dir.mkdir()
                        val imgFile = File(dir, img_name)

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

    return sponsorArray
    }

    override fun onProgressUpdate(vararg values: ArrayList<Sponsor>) {
        super.onProgressUpdate(*values)
            observer.cloudSponsors(values[0])

    }

      override fun onPostExecute(result: ArrayList<Sponsor>) {
        super.onPostExecute(result)
        observer.cloudSponsors(result)
    }
}