package com.mweeksconsulting.lanwarapp.ui

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.VelocityTracker
import com.mweeksconsulting.lanwarapp.R
import com.mweeksconsulting.lanwarapp.Swipe

class MainActivity : AppCompatActivity(), Swipe {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }


    override var mVelocityTracker: VelocityTracker? = null
    override val context: Context = this
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event!=null){
            val xVelocity = mVelocityTracker?.xVelocity
            if (MotionEvent.ACTION_UP == event.actionMasked){
                if (xVelocity != null && (xVelocity > 1000 || xVelocity < -1000)) {
                    println("finish")
                    println("finish:"+xVelocity)
                    finish()
                }


            }
        }
        return super<Swipe>.onTouchEvent(event)


    }



}