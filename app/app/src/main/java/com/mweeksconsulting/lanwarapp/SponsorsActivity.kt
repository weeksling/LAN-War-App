package com.mweeksconsulting.lanwarapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.VelocityTracker
import android.widget.ListView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import java.io.File
import com.google.firebase.storage.FirebaseStorage


class SponsorsActivity : AppCompatActivity(), SponsorObserver,Swipe{

    private var sponsorArray = ArrayList<Sponsor>()
    private val SPONSOR_ORDER_LOCATION = "sponsor_order_location"
    private val UPDATE_DATE = "update_date"
    private lateinit var remoteInstance:FirebaseRemoteConfig

    override val context: Context = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sponsors)
        initalizeSponsors()

    }

    fun initalizeSponsors(){
        val sponsorFile = File(context.filesDir.path + "/" + "sponsorOrder.xml")

        remoteInstance = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
        remoteInstance.setConfigSettings(configSettings)

        //read xml file in background
        val readXmlFile = readSponsorXMLFileInBackground()
        readXmlFile.observer = this
        readXmlFile.execute(sponsorFile)
    }

    //if a file does not exist then the very first thing that should happen is to download the file from firebase
    //get the default values for the remote instance with the local files
    //then fetch the web values
    override fun setDefaultRI(bundle: Bundle) {
        val  default:MutableMap<String,Any> = hashMapOf()
        default.put(SPONSOR_ORDER_LOCATION,bundle.get (SPONSOR_ORDER_LOCATION))
        default.put(UPDATE_DATE,bundle.getLong(UPDATE_DATE))

        remoteInstance.setDefaults(default)
        sponsorArray = bundle.getSerializable("sponsors") as ArrayList<Sponsor>
        println(sponsorArray)
        refreshList()


        var cacheExpiration: Long = 0
        //expire the cache immediately for development mode.
        if (remoteInstance.info.configSettings.isDeveloperModeEnabled()) {
            cacheExpiration = 0
        }

        //fetch the remote instance
        remoteInstance.fetch(cacheExpiration)
                .addOnCompleteListener{
                    task ->
                    //if the fetch was good then get the download uri
                    if (task.isSuccessful) {
                        println("task succesful")
                        remoteInstance.activateFetched()
                        //I want to get the online storage location and download url
                        //TODO set up authentication and change storage rules to private

                        //storage reference
                        val storageStringLocation :String = remoteInstance.getString(SPONSOR_ORDER_LOCATION)
                        val storageRef = FirebaseStorage.getInstance().reference
                        val sponsorPageRef = storageRef.child(storageStringLocation)

                        //storage url
                        //this runs in the background as well
                        //also wish I knew this early on
                        sponsorPageRef.metadata.addOnSuccessListener { storageMetadata ->
                            val cloudUpdateDate =  storageMetadata.updatedTimeMillis
                            val localUpdateDate = remoteInstance.getLong(UPDATE_DATE)
                            remoteInstance.setDefaults(default)
                            updateRemoteConfigValues(storageStringLocation,cloudUpdateDate,localUpdateDate)

                        }
                        println("done observer")
                    } else {
                        println("task not succesful")
                    }
                }.addOnFailureListener {
            println("task failed")
        }

        //get the sponsor xml data download cloud Uri
        println("default remote instance set")
    }

    //decide whether or not to download xml file with pictures
    fun updateRemoteConfigValues(storageStringLocation: String ,cloudUpdateDate:Long,localUpdateDate:Long)
    {
        val sponsorFile = File(filesDir.path + "/" + "sponsorOrder.xml")

        //download from the storage
        if (localUpdateDate!= cloudUpdateDate) {
            println("cloud uri and local uri do not match")
            //download alter and save the xml file, return a list of sponsor objects
            val downloadInBackground = downloadSponsorFilesInBackground()
            downloadInBackground.observer=this
            val map = HashMap<String,Any>()
            map.put(UPDATE_DATE,cloudUpdateDate)
            map.put("file",sponsorFile)
            map.put(SPONSOR_ORDER_LOCATION,storageStringLocation)
            downloadInBackground.execute(map)
        }else{
            println("update dates match")
        }
    }

    //get a set of sponsors from the new data
    override fun cloudSponsors(sponsorArray: ArrayList<Sponsor>) {
        this.sponsorArray = sponsorArray
        println("cloud sponsor array filled")
        refreshList()
    }


    private fun refreshList(){
        val sponsorListView : ListView = findViewById(R.id.sponsorList) as ListView
        sponsorListView.adapter = SponsorAdapter(this,R.id.sponsorList,sponsorArray)
    }

    override var mVelocityTracker: VelocityTracker? = null
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event!=null){
            if (MotionEvent.ACTION_UP == event.actionMasked){
                finish()
            }
        }
        return super<Swipe>.onTouchEvent(event)
    }



}
