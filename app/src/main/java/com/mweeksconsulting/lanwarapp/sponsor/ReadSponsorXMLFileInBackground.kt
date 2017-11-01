package com.mweeksconsulting.lanwarapp.sponsor

import android.os.AsyncTask
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Created by michael on 16/10/17.
 * Reads sponsor file in background
 */
class ReadSponsorXMLFileInBackground(val observer: SponsorObserver, val img_dir:File ):AsyncTask<File,Void,ArrayList<Sponsor>>() {

    override fun doInBackground(vararg sponsorFileArr: File): ArrayList<Sponsor> {

        val sponsorArray = ArrayList<Sponsor>()

        val sponsorFile = sponsorFileArr[0]
        if (sponsorFile.exists()) {
            println("exists")
            val dbFactory = DocumentBuilderFactory.newInstance()
            val docBuilder = dbFactory.newDocumentBuilder()
            val doc = docBuilder.parse(sponsorFile)
            doc.documentElement.normalize()

            //sponsor list
            val sponsorList = doc.getElementsByTagName("sponsor")
            for (i in 0 until sponsorList.length) {
                val node = sponsorList.item(i)
                if (node.nodeType == Node.ELEMENT_NODE) {
                    val element: Element = node as Element
                    val name = element.getElementsByTagName("sponsor_name").item(0).textContent
                    val img_name = element.getElementsByTagName("img_name").item(0).textContent
                    val description = element.getElementsByTagName("description").item(0).textContent
                    println(img_name)
                    val img_path = img_dir.path+"/" + img_name
                    val sponsor = Sponsor(name, description, img_path, observer.context)
                    sponsorArray.add(sponsor)
                    println(img_path)
                }
            }

            if (img_dir.exists() && img_dir.isDirectory) {
                println("img_dir exists")
                val fileList = img_dir.listFiles()

                //get all image files
                val imageFiles = ArrayList<File>()

                //add all files in image
                sponsorArray.forEach { s ->
                    imageFiles.add(File(s.imagePath))
                }

                //delete img file if not in list
                fileList.forEach { file ->
                    if (!imageFiles.contains(file)) {
                        file.delete()
                    }
                }
            } else {
                println("doest not exist")
            }
        }

        return sponsorArray
    }


    override fun onPostExecute(result: ArrayList<Sponsor>?) {
        super.onPostExecute(result)
        println("onp post execute")

        if (result != null) {
            observer.refreshSponsors(result)
        }
    }
}
