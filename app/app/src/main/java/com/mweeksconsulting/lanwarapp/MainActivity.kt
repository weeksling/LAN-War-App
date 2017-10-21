package com.mweeksconsulting.lanwarapp

import android.app.ActionBar
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.view.VelocityTrackerCompat
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import android.view.VelocityTracker

class MainActivity : AppCompatActivity(),Swipe {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    override var mVelocityTracker: VelocityTracker? = null
    override val context: Context = this
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event!=null){
            if (MotionEvent.ACTION_UP == event.actionMasked){
                println("finish")
                finish()
            }
        }
        return super<Swipe>.onTouchEvent(event)


    }



}