package com.mweeksconsulting.lanwarapp.staff

import android.os.AsyncTask
import android.util.Log
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Created by michael on 31/10/17.
 * Reads the staff file in the background
 */
class ReadStaffFileInBackground(val STAFF_ORDER_LOCATION:String,
                                val LOCAL_STAFF_IMG_PATH: String,
                                val  observer: StaffObserver ):
        AsyncTask<File, Void, ArrayList<Staff>>(){


    override fun doInBackground(vararg staffFileArr: File): ArrayList<Staff> {
        val staffArray = ArrayList<Staff>()
        if (staffFileArr[0].exists()) {
            Log.i("StaffFileBackground","staff file exists")

            val dbFactory = DocumentBuilderFactory.newInstance()
            val docBuilder = dbFactory.newDocumentBuilder()
            val doc = docBuilder.parse(staffFileArr[0])
            doc.documentElement.normalize()

            //staff list
            val sponsorList = doc.getElementsByTagName("staff")
            for (i in 0 until sponsorList.length){
                val node = sponsorList.item(i )
                if (node.nodeType == Node.ELEMENT_NODE) {
                    val element : Element = node as Element

                    var name = element.getElementsByTagName("staff_name")?.item(0)?.textContent
                    val img_name = element.getElementsByTagName("img_name")?.item(0)?.textContent
                    var alias = element.getElementsByTagName("alias")?.item(0)?.textContent
                    var role = element.getElementsByTagName("role")?.item(0)?.textContent

                    if (name==null){
                        name="NA"
                    }
                    if (alias==null){
                        alias="NA"
                    }
                    if (role==null){
                        role="NA"
                    }

                    val imgPath = LOCAL_STAFF_IMG_PATH +"/" +img_name
                    val staff = Staff(name, alias, role,imgPath, observer.context)
                    staffArray.add(staff)
                    Log.i("StaffFileImgPath",imgPath)

                }
            }
            val staffImgFile = File(LOCAL_STAFF_IMG_PATH)
            if(staffImgFile.exists() && staffImgFile.isDirectory) {

                Log.i("StaffFileBackground","staff Img file exists")

                val fileList = staffImgFile.listFiles()
                //loop through file list
                //check if sponsor list contains file
                //if it does not contain the file delete
                val imageFiles = ArrayList<File>()
                staffArray.forEach { s ->
                    imageFiles.add(File(s.imgPath))
                }
                fileList.forEach { file ->
                    if (!imageFiles.contains(file)) {
                        file.delete()
                    }
                }
            }else{
                Log.i("StaffFileBackground","staff Img File does not exists")
            }
        }else{
            Log.i("StaffFileBackground","staff file does not exists")

        }
        //return a bundle instead of a remote instance
        return staffArray
    }

    override fun onPostExecute(result: ArrayList<Staff>) {
        super.onPostExecute(result)
        Log.i("Staff File","read xml file post execute")
        Log.i("Staff File result",result.toString())

        observer.refreshStaff(result)
    }
}