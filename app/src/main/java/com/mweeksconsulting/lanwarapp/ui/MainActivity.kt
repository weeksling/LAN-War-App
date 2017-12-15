package com.mweeksconsulting.lanwarapp.ui

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.VelocityTracker
import com.firebase.ui.auth.AuthUI
import com.mweeksconsulting.lanwarapp.R
import com.mweeksconsulting.lanwarapp.Swipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.firebase.ui.auth.ResultCodes
import com.firebase.ui.auth.IdpResponse
import android.content.Intent
import android.util.Log
import com.firebase.ui.auth.ResultCodes.OK


class MainActivity : AppCompatActivity(), Swipe {
    val RC_SIGN_IN = 123
    object Auth{
        var auth :FirebaseAuth?=null
    }

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
        Auth.auth=FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()

        val currentUser = Auth.auth?.currentUser
        if(currentUser==null) {
            val providers: List<AuthUI.IdpConfig> = arrayListOf(
                    AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
            )
            val activity = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build()
            startActivityForResult(activity, RC_SIGN_IN)
        }else{
            Log.i("Main","user already signed in")

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                Log.i("Main","sign in good")
            } else {
                Log.i("Main","sign in bad")
            }
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