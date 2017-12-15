package com.mweeksconsulting.lanwarapp.ui

import com.mweeksconsulting.lanwarapp.R
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import com.mweeksconsulting.lanwarapp.Swipe

class MainActivity : AppCompatActivity(), Swipe {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun openFacebook (view: View) {
        openLink("http://www.facebook.com/LANWARX")

    }

    fun openTwitter (view: View) {
        openLink("https://twitter.com/lanwarx")
    }

    private fun openLink (url:String){
        if (!isConnected()){
            Log.e("openLink", "No connection")
            return
        }

        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        return
        // TODO: Open the actual URI
    }

    private fun isConnected () : Boolean {
        val connManager: ConnectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connManager.activeNetworkInfo
        return networkInfo.isConnected
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
