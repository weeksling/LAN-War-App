package com.mweeksconsulting.lanwarapp.staff

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.VelocityTracker
import android.widget.ListView
import com.mweeksconsulting.lanwarapp.R
import com.mweeksconsulting.lanwarapp.Swipe


class StaffActivity : AppCompatActivity (),StaffObserver,Swipe{
    var staffArray = ArrayList<Staff>()

    //this activity will show a list of all staff members
    //the staff members's roles and the the staff pictures
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_)

        val  readStaffXml = StaffInformation(this)
        readStaffXml.useLocalStaffData()
        readStaffXml.useStaffCloudData()

    }


    //get a set of sponsors from the new data
    override fun refreshStaff(staffArray: ArrayList<Staff>) {
        this.staffArray = staffArray
        refreshList()
    }

    private fun refreshList(){
        val staffListView : ListView = findViewById<ListView>(R.id.staffList) as ListView
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

}
