package com.mweeksconsulting.lanwarapp.Staff_Package;

import android.content.Context
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent
import android.view.VelocityTracker
import android.widget.ListView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.storage.FirebaseStorage

import com.mweeksconsulting.lanwarapp.R;
import com.mweeksconsulting.lanwarapp.Sponsor_Package.*
import com.mweeksconsulting.lanwarapp.Swipe
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class StaffActivity : AppCompatActivity (),StaffObserver,Swipe{
    var staffArray = ArrayList<Staff>()
    private val STAFF_ORDER_LOCATION = "STAFF_ORDER_LOCATION"
    private val UPDATE_DATE = "update_date"
    lateinit var LOCAL_STAFF_FILLE_PATH:String
    lateinit var LOCAL_STAFF_IMG_LOCATION:String

    //this activity will show a list of all staff members
    //the staff members's roles and the the staff pictures
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_)
        initalizeSponsors()
    }

    fun initalizeSponsors(){
        LOCAL_STAFF_FILLE_PATH = filesDir.path + "/" + "staffOrder.xml"
        LOCAL_STAFF_IMG_LOCATION = filesDir.path+"/staff_img"
        val staffFile = File(LOCAL_STAFF_FILLE_PATH)
        //staffFile.delete()
        //read xml file in background
        val readXmlFile = readStaffXml(context,this,UPDATE_DATE,STAFF_ORDER_LOCATION,LOCAL_STAFF_IMG_LOCATION)
        readXmlFile.execute(staffFile)
    }

    //if a file does not exist then the very first thing that should happen is to download the file from firebase
    //get the default values for the remote instance with the local files
    //then fetch the web values
    override fun setDefaultRI(bundle: Bundle) {
        staffArray = bundle.getSerializable("staff") as ArrayList<Staff>
        refreshList()

        val remoteInstance = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build()
        remoteInstance.setConfigSettings(configSettings)

        val  default:MutableMap<String,Any> = hashMapOf()
        default.put(STAFF_ORDER_LOCATION,bundle.get (STAFF_ORDER_LOCATION))
        default.put(UPDATE_DATE,bundle.getLong(UPDATE_DATE))
        remoteInstance.setDefaults(default)


        var cacheExpiration:Long = 3600
        if(remoteInstance.info.configSettings.isDeveloperModeEnabled ){
            cacheExpiration=0
        }
        println("Cache expiration date: " + cacheExpiration)
        remoteInstance.fetch(cacheExpiration)
                .addOnCompleteListener { task ->
                    println("is complete")
                    //if the fetch was good then get the download uri
                    if (task.isSuccessful) {
                        remoteInstance.activateFetched()

                        //I want to get the online storage location and download url
                        //storage reference
                        val storageStringLocation: String = remoteInstance.getString(STAFF_ORDER_LOCATION)
                        println("storageStringLocation:  " + storageStringLocation)
                        val storageRef = FirebaseStorage.getInstance().reference
                        val sponsorPageRef = storageRef.child(storageStringLocation)

                        //storage url
                        //this runs in the background as well
                        //also wish I knew this early on
                        sponsorPageRef.metadata.addOnSuccessListener { storageMetadata ->
                            val cloudUpdateDate = storageMetadata.updatedTimeMillis
                            val localUpdateDate = remoteInstance.getLong(UPDATE_DATE)
                            updateRemoteConfigValues(storageStringLocation,cloudUpdateDate,localUpdateDate)
                        }
                    }
                }.addOnFailureListener {
            println("fail")
        }
        //fetch the remote instance

    }

    //decide whether or not to download xml file with pictures
    fun updateRemoteConfigValues(storageStringLocation: String ,cloudUpdateDate:Long,localUpdateDate:Long)
    {
        //download from the storage
        if (localUpdateDate!= cloudUpdateDate) {
            val sponsorFile = File(LOCAL_STAFF_FILLE_PATH)
            //download alter and save the xml file, return a list of sponsor objects
            println("dates do not match")
            val downloadInBackground = downloadStaffFilesInBackground(this,STAFF_ORDER_LOCATION,UPDATE_DATE,cloudUpdateDate,storageStringLocation,LOCAL_STAFF_IMG_LOCATION)
            downloadInBackground.execute(sponsorFile)
        }else{
            println("dates match")

        }
    }

    //get a set of sponsors from the new data
    override fun cloudSponsors(staffArray: ArrayList<Staff>) {
        this.staffArray = staffArray
        refreshList()
    }

    private fun refreshList(){
        val staffListView : ListView = findViewById(R.id.staffList) as ListView
        staffListView.adapter = StaffAdapter(this, R.id.staffList, staffArray)
    }

    override val context: Context = this
    override var mVelocityTracker: VelocityTracker? = null
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            val xVelocity = mVelocityTracker?.xVelocity
            if (MotionEvent.ACTION_UP == event.actionMasked) {
                if (xVelocity != null && (xVelocity > 1000|| xVelocity < -1000)) {
                    println("finish")
                    println("finish:"+xVelocity)
                    finish()
                }
            }
        }
        return super<Swipe>.onTouchEvent(event)
    }

    //read the xml file and return an array
    internal class readStaffXml(val context: Context,val  observer: StaffObserver,
                                val UPDATE_DATE:String,val STAFF_ORDER_LOCATION:String,
                                val LOCAL_STAFF_IMG_PATH:String ):AsyncTask<File,Void,Bundle>(){


        override fun doInBackground(vararg staffFileArr: File): Bundle {
            val updateDate:Long
            val storageLocation:String
            val staffArray = ArrayList<Staff>()


            if (staffFileArr[0].exists()) {
                val dbFactory = DocumentBuilderFactory.newInstance()
                val docBuilder = dbFactory.newDocumentBuilder()
                val doc = docBuilder.parse(staffFileArr[0])
                doc.documentElement.normalize()

                val root = doc.documentElement
                storageLocation = root.getAttribute(STAFF_ORDER_LOCATION)
                updateDate =  root.getAttribute(UPDATE_DATE).toLong()

                //staff list
                val sponsorList = doc.getElementsByTagName("staff")
                for (i in 0 until sponsorList.length){
                    val node = sponsorList.item(i)
                    if (node.nodeType == Node.ELEMENT_NODE) {
                        val element : Element = node as Element

                        var name = element.getElementsByTagName("staff_name")?.item(0)?.textContent
                        var img_name = element.getElementsByTagName("img_name")?.item(0)?.textContent
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
                    }
                }
                val staffImgFile = File(LOCAL_STAFF_IMG_PATH)
                if(staffImgFile.exists() && staffImgFile.isDirectory) {
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
                }
            } else {
                updateDate = 0
                storageLocation = "THERE IS NO LOCAL FILE"
            }

            //return a bundle instead of a remote instance
            val bundle = Bundle()
            bundle.putString (STAFF_ORDER_LOCATION, storageLocation)
            bundle.putLong(UPDATE_DATE, updateDate)
            bundle.putSerializable("staff",staffArray)
            return bundle
        }

        override fun onPostExecute(result: Bundle) {
            super.onPostExecute(result)
            println("read xml file post execute")
            observer.setDefaultRI(result)
        }



    }


}
