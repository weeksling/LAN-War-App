package com.mweeksconsulting.lanwarapp.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.widget.ListView
import com.mweeksconsulting.lanwarapp.R
import com.mweeksconsulting.lanwarapp.Swipe
import com.mweeksconsulting.lanwarapp.staff.data_handler.StaffAdapter
import com.mweeksconsulting.lanwarapp.staff.Staff
import com.mweeksconsulting.lanwarapp.staff.data_handler.StaffModel


class StaffActivity : AppCompatActivity (), Swipe{
    lateinit var staffViewModel : StaffModel

    //this activity will show a list of all staff members
    //the staff members's roles and the the staff pictures
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_)

        staffViewModel = ViewModelProviders.of(this).get(StaffModel::class.java)

        Log.i("staff activity", "on create")

        // Create the observer which updates the UI.
        staffViewModel.getStaff()?.observe(this, Observer<List<Staff>> {
            newSponsors ->
            if(newSponsors != null) {
                Log.i("staff activity: " ,"refresh list")

                refreshList(newSponsors)
            }else{
                Log.i("staff activity: " ,"do not refresh list")
            }
        })

        Log.i("staff activity", "bottom of on create")

    }


    private fun refreshList(newStaff:List<Staff>){
        Log.i("staff activity", R.id.staffList.toString())
        val listview : ListView = findViewById<ListView>(R.id.staffList)
        val adapter = StaffAdapter(this, R.layout.staff_list_layout, newStaff)
        listview.adapter=adapter
    }




    override val context: Context = this
    override var mVelocityTracker: VelocityTracker? = null
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        println("MOTION")
        if (event != null) {
            val xVelocity = mVelocityTracker?.xVelocity
            if (MotionEvent.ACTION_UP == event.actionMasked) {
                if (xVelocity != null && (xVelocity > 1000|| xVelocity < -1000)) {
                    finish()
                }
            }
        }
        return super<Swipe>.onTouchEvent(event)
    }
}
