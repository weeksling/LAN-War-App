package com.mweeksconsulting.lanwarapp.sponsor

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.VelocityTracker
import android.widget.ListView
import com.mweeksconsulting.lanwarapp.R
import com.mweeksconsulting.lanwarapp.Swipe


class SponsorsActivity : AppCompatActivity(), SponsorObserver, Swipe {

    private var sponsorArray = ArrayList<Sponsor>()
      override val context: Context = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sponsors)
        val sponsorData = SponsorInformation(this)
        sponsorData.useLocalSponsorData()
        sponsorData.useCloudData()
    }



    //get a set of sponsors from the new data
    override fun refreshSponsors(sponsorArray: ArrayList<Sponsor>) {
        println("refresh sponsor")
        this.sponsorArray = sponsorArray
        refreshList()
    }

    private fun refreshList(){
        val sponsorListView : ListView = findViewById<ListView>(R.id.sponsorList) as ListView
        sponsorListView.adapter = SponsorAdapter(this, R.id.sponsorList, sponsorArray)
    }

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
