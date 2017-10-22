package com.mweeksconsulting.lanwarapp.Staff_Package

import android.content.Context
import android.os.AsyncTask
import com.google.firebase.storage.FirebaseStorage
import com.mweeksconsulting.lanwarapp.Sponsor_Package.Sponsor
import com.mweeksconsulting.lanwarapp.Sponsor_Package.SponsorObserver
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import java.io.Serializable
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * Created by michael on 17/10/17.
 */
class downloadStaffFilesInBackground (val  observer: StaffObserver, val STAFF_ORDER_LOCATION: String, val UPDATE_DATE:String,
                                      val date:Long,val storageLocation:String, val LOCAL_STAFF_IMG_LOCATION:String)
    : AsyncTask<File, ArrayList<Staff>, ArrayList<Staff>>() {
    val staffArray = ArrayList<Staff>()

    override fun doInBackground(vararg p0: File): ArrayList<Staff> {
        val staffFile = p0[0]
        val storageRef = FirebaseStorage.getInstance().reference
        val sponsorPageRef = storageRef.child(storageLocation)

        sponsorPageRef.getFile(staffFile).addOnSuccessListener {
                val docFactory = DocumentBuilderFactory.newInstance()
                val docBuilder = docFactory.newDocumentBuilder()
                val doc = docBuilder.parse(staffFile)
                doc.documentElement.normalize()

                val rootElement = doc.documentElement
                rootElement.setAttribute(STAFF_ORDER_LOCATION,storageLocation)
                rootElement.setAttribute(UPDATE_DATE,date.toString())

                //save data to file
                val transformer = TransformerFactory.newInstance().newTransformer()
                val output = StreamResult(staffFile)
                val input = DOMSource(doc)
                transformer.transform(input, output)

                val sponsorList = doc.getElementsByTagName("staff")
                for (i in 0 until sponsorList.length){
                    val node = sponsorList.item(i)
                    if (node.nodeType == Node.ELEMENT_NODE) {
                        val attritubeNodes = node as Element

                        var name = attritubeNodes.getElementsByTagName("staff_name")?.item(0)?.textContent
                        var img_name = attritubeNodes.getElementsByTagName("img_name")?.item(0)?.textContent
                        var alias = attritubeNodes.getElementsByTagName("alias")?.item(0)?.textContent
                        var role = attritubeNodes.getElementsByTagName("role")?.item(0)?.textContent

                        if (name==null || name ==""){
                            name="NA"
                        }
                        if (alias==null || alias ==""){
                            alias="NA"
                        }
                        if (role==null|| role==""){
                            role="NA"
                        }
                        if (img_name==null || img_name==""){
                            img_name = "NA"
                        }

                        val nameText:String = name
                        val aliasText:String = alias
                        val roleText:String = role
                        val img_name_text:String = img_name


                        //imageLocation
                        // download this file in the background
                            val imageFolder = storageRef.child("staff_pictures")
                            val dir = File(LOCAL_STAFF_IMG_LOCATION)
                            dir.mkdir()
                            val imgFile = File(dir, img_name_text)

                        if(img_name_text!="NA") {
                            val imgRef = imageFolder.child(img_name_text)
                            imgRef.getFile(imgFile).addOnSuccessListener {
                                val staff = Staff(nameText, aliasText, roleText, imgFile.path, observer.context)
                                staffArray.add(staff)
                                publishProgress(staffArray)
                            }.addOnFailureListener {
                                val staff = Staff(nameText, aliasText, roleText, "NO CLOUD FILE", observer.context)
                                staffArray.add(staff)
                                publishProgress(staffArray)
                            }
                        }else{
                            val staff = Staff(nameText, aliasText, roleText, "NO LOCAL FILE", observer.context)
                            staffArray.add(staff)
                            publishProgress(staffArray)
                        }
                    }
                }
        }

    return staffArray
    }

    override fun onProgressUpdate(vararg values: ArrayList<Staff>) {
        super.onProgressUpdate(*values)
            observer.cloudSponsors(values[0])

    }

      override fun onPostExecute(result: ArrayList<Staff>) {
        super.onPostExecute(result)
        observer.cloudSponsors(result)
    }
}