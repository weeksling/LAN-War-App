package com.mweeksconsulting.lanwarapp.Sponsor_Package

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.VelocityTracker
import android.widget.ListView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import java.io.File
import com.google.firebase.storage.FirebaseStorage
import com.mweeksconsulting.lanwarapp.BuildConfig
import com.mweeksconsulting.lanwarapp.R
import com.mweeksconsulting.lanwarapp.Swipe


class SponsorsActivity : AppCompatActivity(), SponsorObserver, Swipe {

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


        //fetch the remote instance
        remoteInstance.fetch()
                .addOnCompleteListener{
                    task ->
                    //if the fetch was good then get the download uri
                    if (task.isSuccessful) {
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
                    }
                }

    }

    //decide whether or not to download xml file with pictures
    fun updateRemoteConfigValues(storageStringLocation: String ,cloudUpdateDate:Long,localUpdateDate:Long)
    {
        val sponsorFile = File(filesDir.path + "/" + "sponsorOrder.xml")

        //download from the storage
        if (localUpdateDate!= cloudUpdateDate) {
            //download alter and save the xml file, return a list of sponsor objects
            val downloadInBackground = downloadSponsorFilesInBackground()
            downloadInBackground.observer=this
            val map = HashMap<String,Any>()
            map.put(UPDATE_DATE,cloudUpdateDate)
            map.put("file",sponsorFile)
            map.put(SPONSOR_ORDER_LOCATION,storageStringLocation)
            downloadInBackground.execute(map)
        }
    }

    //get a set of sponsors from the new data
    override fun cloudSponsors(sponsorArray: ArrayList<Sponsor>) {
        this.sponsorArray = sponsorArray
        refreshList()
    }


    private fun refreshList(){
        val sponsorListView : ListView = findViewById(R.id.sponsorList) as ListView
        sponsorListView.adapter = SponsorAdapter(this, R.id.sponsorList, sponsorArray)
    }

    override var mVelocityTracker: VelocityTracker? = null
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event!=null){
            val xVelocity = mVelocityTracker?.xVelocity
            if (MotionEvent.ACTION_UP == event.actionMasked){
                if (xVelocity != null && (xVelocity > 1000|| xVelocity < -1000)) {
                    println("finish")
                    println("finish:"+xVelocity)
                    finish()
                }            }
        }
        return super<Swipe>.onTouchEvent(event)
    }



}
