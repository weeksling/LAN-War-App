package com.mweeksconsulting.lanwarapp.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import com.mweeksconsulting.lanwarapp.*
import com.mweeksconsulting.lanwarapp.staff.data_handler.StaffAdapter
import com.mweeksconsulting.lanwarapp.staff.Staff
import com.mweeksconsulting.lanwarapp.staff.data_handler.StaffModel
import java.util.concurrent.Executors


class StaffActivity : AppCompatActivity (), Swipe{
    lateinit var staffViewModel : StaffModel
    lateinit var netWorkReceiver:StaffNetwork
    var netWorkReceiverReg=false
    var staffList:List<Staff>? = null

    //this activity will show a list of all staff members
    //the staff members's roles and the the staff pictures
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_layout)

        // Registers BroadcastReceiver to track network connection changes.
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        netWorkReceiver = StaffNetwork()
        this.registerReceiver(netWorkReceiver, filter)
        netWorkReceiverReg=true



        staffViewModel = ViewModelProviders.of(this).get(StaffModel::class.java)

        Log.i("staff activity", "on create")

        // Create the observer which updates the UI.
        staffViewModel.getStaff()?.observe(this, Observer<List<Staff>> {
            newStaff ->
            if(newStaff != null && newStaff.isNotEmpty()) {
                staffList = newStaff
                Log.i("staff activity: " ,"refresh list")

                refreshList(newStaff)
            }else{
                Log.i("staff activity: " ,"do not refresh list")
            }
        })

        Log.i("staff activity", "bottom of on create")

    }

    //network connection changed
    fun retry(networkInfo: NetworkInfo?) {
        val isConnected = networkInfo?.isConnected

        if(staffList==null) {
            if (networkInfo == null || (isConnected != null && !isConnected)) {
                setContentView(R.layout.loading_layout)
                val progressBar = findViewById<ProgressBar>(R.id.progressBar)
                val detailText = findViewById<TextView>(R.id.details)
                progressBar.visibility = View.INVISIBLE
                detailText.text = "No Network connection"
            } else {
                setContentView(R.layout.loading_layout)
                val detailText = findViewById<TextView>(R.id.details)
                detailText.text = "Downloading Staff Information"
            }
        }
        Executors.newSingleThreadExecutor().execute(LanWarApplication.appSingleton.staffRepo)

    }


    private fun refreshList(newStaff:List<Staff>){
        setContentView(R.layout.activity_staff_)

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

    override fun onPause() {
        super.onPause()
        if(netWorkReceiverReg) {
            unregisterReceiver(netWorkReceiver)
            netWorkReceiverReg=false
        }
    }



    inner class StaffNetwork: BroadcastReceiver() {
        override fun onReceive(context: Context, p1: Intent?) {
            //https://developer.android.com/training/basics/network-ops/managing.html
            val connManager:ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connManager.activeNetworkInfo
            Log.i("Network Manager","On Recive")
            retry(networkInfo)
        }
    }
}
