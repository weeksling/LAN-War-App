package com.mweeksconsulting.lanwarapp.ui

import android.arch.lifecycle.*
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ListView
import com.mweeksconsulting.lanwarapp.R
import com.mweeksconsulting.lanwarapp.Swipe
import android.arch.lifecycle.ViewModelProviders
import com.mweeksconsulting.lanwarapp.sponsor.Sponsor
import com.mweeksconsulting.lanwarapp.sponsor.data_handler.SponsorAdapter
import com.mweeksconsulting.lanwarapp.sponsor.data_handler.SponsorModel
import android.content.Intent
import android.net.Uri
import com.mweeksconsulting.lanwarapp.LanWarApplication


class SponsorsActivity : AppCompatActivity(),Swipe  {
    lateinit var sponsorViewModel : SponsorModel
    lateinit var listview : ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sponsors)
        listview =  findViewById<ListView>(R.id.sponsorList)

        sponsorViewModel = ViewModelProviders.of(this).get(SponsorModel::class.java)

        Log.i("sponsor activity", "on create")

        // Create the observer which updates the UI.
        sponsorViewModel.getSponsors()?.observe(this, Observer<List<Sponsor>> {
            newSponsors ->
            if(newSponsors != null) {
                Log.i("sponsor activity: " ,"refresh list")

                refreshList(newSponsors)
            }else{
                Log.i("sponsor activity: " ,"do not refresh list")
            }
        })

        Log.i("sponsor activity", "bottom of on create")

        listview.setOnItemClickListener { adapterView, view, i, l ->
            println("we have a click")
            val sponsor :Sponsor = adapterView.getItemAtPosition(i) as Sponsor
            Log.i("Sponsor activity",sponsor.toString())
            val path = sponsor.webSite
            if(path != null) {
              //  val uri =  LaunchWebPage(path).execute().get()
                val uri =       Uri.parse(path)

                val activeNetwork = LanWarApplication.appSingleton.connectionManager.activeNetworkInfo
                val isConnected = activeNetwork != null && activeNetwork.isConnected

                if (uri != null && isConnected){
                    val webIntent =Intent(Intent.ACTION_VIEW,uri)
                    startActivity(webIntent)
                }
            }
        }
    }


    private fun refreshList(newSponsors:List<Sponsor>){
        Log.i("sponsor activity", R.id.sponsorList.toString())
        val adapter = SponsorAdapter(this, R.layout.sponsor_linear_layout, newSponsors)
        listview.adapter=adapter
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
}
