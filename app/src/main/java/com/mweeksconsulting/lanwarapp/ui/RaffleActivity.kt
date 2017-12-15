package com.mweeksconsulting.lanwarapp.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.arch.lifecycle.Observer
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.mweeksconsulting.lanwarapp.R
import com.mweeksconsulting.lanwarapp.raffle.*
import com.mweeksconsulting.lanwarapp.raffle.ScreenSlidePageFragment
import android.content.Intent
import android.view.MotionEvent
import android.view.SoundEffectConstants.CLICK
import android.view.VelocityTracker
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.mweeksconsulting.lanwarapp.Swipe
import java.text.SimpleDateFormat
import java.util.*

//Used luigi http://piq.codeus.net/picture/288905/faceplant
class RaffleActivity : AppCompatActivity(),Swipe {

    lateinit var pager :ViewPager
    lateinit var pageAdapter: ScreenSlidePageAdapter
    val raffleViewModel = RaffleModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_raffle_activity)

        val NOTIFIED_KEY = "NOTIFIED"
        val sharedPref = this.getPreferences(android.content.Context.MODE_PRIVATE)
        val notified = sharedPref.getBoolean(NOTIFIED_KEY,false)
        //update UI
        val check_mark = findViewById<ImageView>(R.id.check_mark)
        if(notified) {
            check_mark.visibility = View.VISIBLE
        }else{
            check_mark.visibility = View.GONE
        }




        raffleViewModel.getRaffles()?.observe(this, Observer<List<Raffle>> { newRaffleData ->
            if (newRaffleData != null && newRaffleData.isNotEmpty()) {
                Log.i("raffle activity: ", "refresh list")
                refreshScreen()
            } else {
                Log.i("raffle activity: ", "do not refresh list")
            }
        })

    }

    fun refreshScreen(){
        val raffle : Raffle? = raffleViewModel.getCurrentRaffle()

        var items : Array<Item>? =null
        var size =0
        if(raffle!=null) {
              items = LoadRafflesFromDB.LoadRaffleItems(raffle.id).execute().get()
              size = LoadRafflesFromDB.CountRaffleItems(raffle.id).execute().get()
              Log.i("Raffle actitiy", LoadRafflesFromDB.LoadRaffleItems(raffle.id).execute().get()?.size.toString())
            Log.i("Raffle actitiy","raffle is not null")
        }else{
            Log.i("Raffle actitiy","raffle is  null")
        }

        Log.i("Raffle actitiy", "all items count" + LoadRafflesFromDB.CountItems().execute().get().toString())

        pager = findViewById<ViewPager>(R.id.ImageGallery)
        pageAdapter = ScreenSlidePageAdapter(supportFragmentManager,size,items)
        pager.adapter=pageAdapter

        pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
            }
        })
        val pattern = "hh:mm a"


        Log.i("current raffle: ", raffle.toString())
        var time = if (raffle?.raffleTime!=null) raffle.raffleTime else ""
        val date = if (raffle?.raffleDate!=null) raffle.raffleDate else ""
        val loc = if (raffle?.location!=null) raffle.location else ""

        val timeView = findViewById<TextView>(R.id.Time)
        val dateView = findViewById<TextView>(R.id.Raffle_Date)
        val locationView = findViewById<TextView>(R.id.Location)
        val timeLabel = findViewById<TextView>(R.id.time_label)
        val dateLabel = findViewById<TextView>(R.id.date_lable)
        val locationLabel = findViewById<TextView>(R.id.loc_label)

        val timeFortmat = SimpleDateFormat(pattern)
        val toDate = SimpleDateFormat("hh:mm")
        if(time!="") {
            timeView.text = timeFortmat.format(toDate.parse(time))
        }else{
            timeView.text = ""

        }
        dateView.text=date
        locationView.text=loc

        //notify the UI
        val notify_button = findViewById<Button>(R.id.notify_button)
        notify_button.playSoundEffect(CLICK)
        notify_button.setOnClickListener{

            val NOTIFIED_KEY = "NOTIFIED"
            val sharedPref = this.getPreferences(android.content.Context.MODE_PRIVATE)
            var notified = sharedPref.getBoolean(NOTIFIED_KEY,false)
            notified = if(notified)false else true
            val editor = sharedPref.edit()
            editor.putBoolean(NOTIFIED_KEY,notified)
            editor.apply()
            //update UI
            val check_mark = findViewById<ImageView>(R.id.check_mark)
            val duration = Toast.LENGTH_SHORT
            if(notified) {
                check_mark.visibility = View.VISIBLE
                val text = "Registered for the raffle!"
                val toast = Toast.makeText(this, text, duration)
                toast.show()
            }else{
                val text = "unregistered for the raffle!"
                check_mark.visibility = View.GONE
                val toast = Toast.makeText(this, text, duration)
                toast.show()
            }
        }

        if(raffle!=null) {
            timeLabel.visibility=View.VISIBLE
            dateLabel.visibility=View.VISIBLE
            locationLabel.visibility=View.VISIBLE
            pager.background=  null //@android:color/transparent


            //new alarm
            val pattern = "dd-MM-yyyy hh:mm a"
            val dateFormat = SimpleDateFormat(pattern)
            val input = date + time

            Log.i("Raffle Activity", "input $input")
            val raffleDate = dateFormat.parse(input)

            val sharedPref = this.getPreferences(android.content.Context.MODE_PRIVATE)
            val RAFFLE_TIME_KEY = "TIME"

            //if the stored date is before the next raffle date
            //store a new distant raffle
            //if(storedDate.before(raffleDate)){
            Log.i("Raffle Activity", "stored data before raffleData")
            Log.i("Raffle Activity", "time : $time")
            Log.i("Raffle Activity", "raffle date $raffleDate")


            val editor = sharedPref.edit()
            editor.putString(RAFFLE_TIME_KEY, input)
            editor.apply()

            //want RTC clock
            val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(this, RaffleReciver::class.java)
            intent.putExtra("TIME",input)
            val cal = Calendar.getInstance()
            cal.time= raffleDate
            cal.add(Calendar.HOUR,-1)
            val setAlarm = cal.time.time

            Log.i("Raffle Activity", "raffle date before${Date(setAlarm)})")
            val alarmIntent: PendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
            am.set(AlarmManager.RTC_WAKEUP, setAlarm, alarmIntent)
            //}
        }else{
            timeLabel.visibility=View.INVISIBLE
            dateLabel.visibility=View.INVISIBLE
            locationLabel.visibility=View.INVISIBLE
            pager.background=getDrawable(R.drawable.no_new_raffles)



        }

    }

    inner class ScreenSlidePageAdapter(fm:FragmentManager, val size:Int, val items: Array<Item>?) : FragmentStatePagerAdapter(fm){


        override fun getCount(): Int {
            return  size
        }

        override fun getItem(position: Int): Fragment {
            val f = ScreenSlidePageFragment.Instance.getFragment(items,position)
            return f
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
