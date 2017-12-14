package com.mweeksconsulting.lanwarapp.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
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

        val path = "https://www.twitch.tv/faraazkhan"
                //  val uri =  LaunchWebPage(path).execute().get()
        val uri =       Uri.parse(path)
        val connManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connManager.activeNetworkInfo
        val isConnected = networkInfo?.isConnected

        if (uri != null && isConnected != null && isConnected){
        //   val webIntent = Intent(Intent.ACTION_VIEW,uri)
        //   startActivity(webIntent)
        }
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