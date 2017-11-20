package com.mweeksconsulting.lanwarapp.sponsor.ui

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
import com.mweeksconsulting.lanwarapp.sponsor.data_handler.StaffViewModel


class SponsorsActivity : AppCompatActivity(),Swipe  {



    lateinit var sponsorViewModel : StaffViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sponsors)
        sponsorViewModel = ViewModelProviders.of(this).get(StaffViewModel::class.java)

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

    }


    private fun refreshList(newSponsors:List<Sponsor>){
        Log.i("sponsor activity", R.id.sponsorList.toString())
        val listview : ListView = findViewById<ListView>(R.id.sponsorList)
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
