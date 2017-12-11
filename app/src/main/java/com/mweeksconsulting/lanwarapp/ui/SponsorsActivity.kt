package com.mweeksconsulting.lanwarapp.ui

import android.arch.lifecycle.*
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ListView
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import com.mweeksconsulting.lanwarapp.sponsor.Sponsor
import com.mweeksconsulting.lanwarapp.sponsor.data_handler.SponsorAdapter
import com.mweeksconsulting.lanwarapp.sponsor.data_handler.SponsorModel
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.widget.ProgressBar
import android.widget.TextView
import com.mweeksconsulting.lanwarapp.*
import com.mweeksconsulting.lanwarapp.R
import java.util.concurrent.Executors


class SponsorsActivity : AppCompatActivity(),Swipe {
    lateinit var sponsorViewModel : SponsorModel
    lateinit var netWorkReceiver: SponsorNetwork
    var sponsorList:List<Sponsor>?=null
    var netWorkReceiverReg = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_layout)
        //check if the internet is dead

        // Registers BroadcastReceiver to track network connection changes.
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        netWorkReceiver = SponsorNetwork()
        this.registerReceiver(netWorkReceiver, filter)
        netWorkReceiverReg=true

        sponsorViewModel = ViewModelProviders.of(this).get(SponsorModel::class.java)
        Log.i("sponsor activity", "on create")

        // Create the observer which updates the UI.
        sponsorViewModel.getSponsors()?.observe(this, Observer<List<Sponsor>> {
            newSponsors ->
            if(newSponsors != null && newSponsors.isNotEmpty()) {
                sponsorList=newSponsors
                Log.i("sponsor activity: " ,"refresh list")

                refreshList(newSponsors)
            }else{
                Log.i("sponsor activity: " ,"do not refresh list")
            }
        })
        Log.i("sponsor activity", "bottom of on create")

    }

    private fun refreshList(newSponsors:List<Sponsor>){
        setContentView(R.layout.activity_sponsors)
        val listview  = findViewById<ListView>(R.id.sponsorList)

        Log.i("sponsor activity", R.id.sponsorList.toString())
        val adapter = SponsorAdapter(this, R.layout.sponsor_linear_layout, newSponsors)
        listview.adapter=adapter

        listview.setOnItemClickListener { adapterView, view, i, l ->
            println("we have a click")
            val sponsor :Sponsor = adapterView.getItemAtPosition(i) as Sponsor
            Log.i("Sponsor activity",sponsor.toString())
            val path = sponsor.webSite
            if(path != null) {
                val connManager:ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = connManager.activeNetworkInfo
                val isConnected = networkInfo?.isConnected

                val uri = Uri.parse(path)
                if (uri != null && networkInfo!=null || (isConnected!= null && !isConnected)){
                    val webIntent =Intent(Intent.ACTION_VIEW,uri)
                    startActivity(webIntent) }
            }
        }



    }

    override fun onPause() {
        super.onPause()
        if(netWorkReceiverReg) {
            unregisterReceiver(netWorkReceiver)
            netWorkReceiverReg=false
        }
    }

    //network connection changed
    fun retry(networkInfo: NetworkInfo?) {
        val isConnected = networkInfo?.isConnected

        if(sponsorList==null) {
            if (networkInfo == null || (isConnected != null && !isConnected)) {
                setContentView(R.layout.loading_layout)
                val progressBar = findViewById<ProgressBar>(R.id.progressBar)
                val detailText = findViewById<TextView>(R.id.details)
                progressBar.visibility = View.INVISIBLE
                detailText.text = "No Network connection"
            } else {
                setContentView(R.layout.loading_layout)
                val detailText = findViewById<TextView>(R.id.details)
                detailText.text = "Downloading Sponsor Information"
            }
        }
        Executors.newSingleThreadExecutor().execute(LanWarApplication.appSingleton.sponsorRepo)

    }


    override val context: Context = this
    override var mVelocityTracker: VelocityTracker? = null
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        println("sponsors")
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

    inner class SponsorNetwork: BroadcastReceiver() {
        override fun onReceive(context: Context, p1: Intent?) {
            val connManager:ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connManager.activeNetworkInfo
            Log.i("Network Manager","On Recive")
            retry(networkInfo)
        }
    }

}
