package com.mweeksconsulting.lanwarapp.raffle

import android.annotation.TargetApi
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.mweeksconsulting.lanwarapp.R
import java.text.SimpleDateFormat
import java.util.*
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build
import android.media.RingtoneManager
import android.app.PendingIntent
import com.mweeksconsulting.lanwarapp.ui.RaffleActivity


/**
 * Created by michael on 14/12/17.
 * this class receiver the raffle alarm intent that is set to it
 * sends a notification when the even is 1 hour away or 10 minutes away
 */
class RaffleReciver:BroadcastReceiver() {

    //decide which notification to send
    override fun onReceive(context: Context, intent: Intent) {
        Log.i("Raffle Reciever","raffled on Reveive")
        val dateString = intent.getStringExtra("TIME")
        val pattern1 = "dd-MM-yyyy hh:mm a"
        val dateFormat1 = SimpleDateFormat(pattern1)
        val date = dateFormat1.parse(dateString)

        val now = Calendar.getInstance() as Calendar

        val tenMinutesBeforeRaffle = Date(date.time-(Calendar.MINUTE*10))

        if(now.time.before(tenMinutesBeforeRaffle)){
            distanceNotification(date,context)
        }else{
            soonNotification(date,context)
        }

    }

    //send a reminder notification
    fun distanceNotification(raffleDate: Date, context: Context){
        Log.i("Raffle Reciever","distant notification")

        val notifyID = 1;
        val CHANNEL_ID = "Raffle_channel_id";// The id of the channel.
        val name = "LANWAR"

        val id = "raffle_id"
        val currentDate = Calendar.getInstance().time
        val dateDiff = Date(raffleDate.time-currentDate.time)
        val bodyText=raffleMessage(raffleDate) + "\n" + dateDiff.time

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val contentIntent = PendingIntent.getActivity(context, 0, Intent(context, RaffleActivity::class.java), 0)

        @TargetApi(26)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID,name,NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        Log.i("Raffle Reciever","distant notification")
        val notification = NotificationCompat.Builder(context,id)
                .setContentTitle("Lanwar Raffle")
                .setContentIntent(contentIntent)
                .setContentText(bodyText)
                .setSmallIcon(R.mipmap.lanwar_icon)
                .setOnlyAlertOnce(true)
                .setLights(Color.RED, 3000, 3000)
                .setChannelId(CHANNEL_ID).build()
        notificationManager.notify(notifyID,notification)

        Log.i("Raffle Reciever","built notification")

        //want RTC clock
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context,RaffleReciver::class.java)

        val cal = Calendar.getInstance()
        cal.time= raffleDate
        cal.add(Calendar.MINUTE,-10)
        val setAlarm = cal.time.time


        val tenMinutesBeforeRaffle = Date(setAlarm)

        val alarmIntent : PendingIntent = PendingIntent.getBroadcast(context,0,intent,0)
        am.set(AlarmManager.RTC_WAKEUP, tenMinutesBeforeRaffle.time, alarmIntent)
    }

    //send an urgent reminder notification
    fun soonNotification(raffleDate: Date ,context: Context){
        Log.i("Raffle Reciever","soon notification")
        val notifyID = 1;
        val CHANNEL_ID = "Raffle_channel_id";// The id of the channel.
        val name = "LANWAR"

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val contentIntent = PendingIntent.getActivity(context, 0, Intent(context, RaffleActivity::class.java), 0)



        @TargetApi(26)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID,name,NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        //
        val v= longArrayOf(0, 10, 0, 10, 0,0, 100, 0, 100, 0,0, 100, 0, 100, 0,0, 100, 0, 100, 0)
        val alarm =        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        val id = "raffle_id"
        val bodyText=raffleMessage(raffleDate)

        val notification = NotificationCompat.Builder(context,id)
                .setContentTitle("Lanwar Raffle")
                .setContentIntent(contentIntent)
                .setContentText(bodyText)
                .setSmallIcon(R.mipmap.lanwar_icon)
                .setOnlyAlertOnce(true)
                .setSound(alarm)
                .setVibrate(v)
                .setLights(Color.RED, 3000, 3000)
                .setChannelId(CHANNEL_ID).build()

        notificationManager.notify(notifyID,notification)

    }





    fun raffleMessage(currentRaffleTime:Date):String{
        val ct = Calendar.getInstance() as Calendar
        val pattern = "hh:mm a"
        val dateFormat = SimpleDateFormat(pattern)

        if(currentRaffleTime.before(ct.time)){
            return "There is a raffle going on now!"
        }
        return "The next raffle is at  ${dateFormat.format(currentRaffleTime)}"
    }
}