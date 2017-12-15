package com.mweeksconsulting.lanwarapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.MotionEvent
import android.view.VelocityTracker
import com.mweeksconsulting.lanwarapp.ui.SwipeNavActivity
import com.mweeksconsulting.lanwarapp.ui.SponsorsActivity
import com.mweeksconsulting.lanwarapp.ui.StaffActivity
import java.util.*

/**
 * Created by michael on 21/10/17.
 */
interface Swipe{
    var mVelocityTracker: VelocityTracker?
    val context:Context


    fun onTouchEvent(event: MotionEvent?): Boolean {

        if (event!=null) {
            val action = event.actionMasked
            when (action) {
                MotionEvent.ACTION_DOWN -> {

                    when (mVelocityTracker) {
                        null -> mVelocityTracker = VelocityTracker.obtain()
                        else -> mVelocityTracker?.clear()
                    }
                    mVelocityTracker?.addMovement(event)
                }
                MotionEvent.ACTION_MOVE -> {
                    mVelocityTracker?.addMovement(event)
                    mVelocityTracker?.computeCurrentVelocity(1000)

                }
                MotionEvent.ACTION_UP -> {
                    val xVelocity = mVelocityTracker?.xVelocity

                    val mainClass = SwipeNavActivity::class.java
                    val sponsorShipClass = SponsorsActivity::class.java
                    val staffClass = StaffActivity::class.java


                    val activityMap = TreeMap<Int,Any>()
                    activityMap.put(0,mainClass)
                    activityMap.put(1,sponsorShipClass)
                    activityMap.put(2,staffClass)


                    var position:Int =
                    when(context.javaClass){
                        mainClass->0
                        sponsorShipClass->1
                        staffClass->2

                        else->-1
                    }
                    val size = activityMap.size

                    if(position!= -1) {
                        if (xVelocity != null && xVelocity > 1000) {
                            position++
                            if (position >= size) {
                                position = 0
                            }

                            val cls:Class<Activity> = activityMap[position]as Class<Activity>
                            val intent = Intent(context,cls)

                            context.startActivity(intent)
                        }//right swipe
                        else if (xVelocity != null && xVelocity < -1000) {
                            position--
                            if (position < 0) {
                                position = size-1
                            }
                            val cls:Class<Activity> = activityMap[position]as Class<Activity>
                            val intent = Intent(context, cls)
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }
        return true
    }




}