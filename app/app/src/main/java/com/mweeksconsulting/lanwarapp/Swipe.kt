package com.mweeksconsulting.lanwarapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.VelocityTracker
import java.util.*
import kotlin.collections.ArrayList

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
                  //  mVelocityTracker?.recycle()
                    val xVelocity = mVelocityTracker?.xVelocity

                    val mainClass = MainActivity::class.java
                    val sponsorShipClass = SponsorsActivity::class.java

                    val maap = TreeMap<Int,Any>()
                    maap.put(0,mainClass)
                    maap.put(1,sponsorShipClass)

                    var position:Int =
                    when(context.javaClass){
                        sponsorShipClass->1
                        mainClass->0
                        else->-1
                    }
                    val size = maap.size

                    if(position!= -1) {
                        if (xVelocity != null && xVelocity > 1000) {
                            println("right swipe")
                            position++
                            println("old swipe :  " + position)
                            if (position >= size) {
                                position = 0
                            }
                            println("new swipe:  "+position)

                            val cls:Class<Activity> = maap[position]as Class<Activity>
                            val intent = Intent(context,cls)

                            context.startActivity(intent)
                        }//right swipe
                        else if (xVelocity != null && xVelocity < 1000) {
                            println("left swipe")
                            position--
                            println("old swipe :  " + position)
                            if (position < 0) {
                                position = size-1
                            }
                            println("new swipe:  "+position)

                            val cls:Class<Activity> = maap[position]as Class<Activity>
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