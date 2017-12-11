package com.mweeksconsulting.lanwarapp

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import android.util.Log
import com.mweeksconsulting.lanwarapp.LanWarApplication.appSingleton.DB
import com.mweeksconsulting.lanwarapp.LanWarApplication.appSingleton.context
import com.mweeksconsulting.lanwarapp.LanWarApplication.appSingleton.sponsorDAO
import com.mweeksconsulting.lanwarapp.LanWarApplication.appSingleton.sponsorRepo
import com.mweeksconsulting.lanwarapp.LanWarApplication.appSingleton.staffDAO
import com.mweeksconsulting.lanwarapp.LanWarApplication.appSingleton.staffRepo
import com.mweeksconsulting.lanwarapp.sponsor.data_handler.SponsorRepo
import com.mweeksconsulting.lanwarapp.room.LanWarDatabase
import com.mweeksconsulting.lanwarapp.room.SponsorDAO
import com.mweeksconsulting.lanwarapp.sponsor.data.StaffDAO
import com.mweeksconsulting.lanwarapp.staff.data_handler.StaffRepo
import java.util.concurrent.Executors
import android.net.ConnectivityManager
import android.content.IntentFilter





class LanWarApplication : Application() {

    object appSingleton{
        lateinit var DB : LanWarDatabase
       lateinit var context : Context
        lateinit var sponsorDAO : SponsorDAO
        lateinit var staffDAO : StaffDAO
        lateinit var sponsorRepo : SponsorRepo
        lateinit var staffRepo : StaffRepo

    }

    override fun onCreate() {
        super.onCreate()
        DB = Room.databaseBuilder(this, LanWarDatabase::class.java, "LanWarDataBase").build()
        context=this.applicationContext
        sponsorDAO = DB.sponsorDao
        staffDAO=DB.staffDAO
        //initalize sponsor repo when app is first opened
        sponsorRepo = SponsorRepo()
        staffRepo = StaffRepo()
        //initalize data when app loads in background
        Executors.newSingleThreadExecutor().execute(sponsorRepo)
        Executors.newSingleThreadExecutor().execute(staffRepo)
        Log.i("Lan war app","On Create")

    }

}
