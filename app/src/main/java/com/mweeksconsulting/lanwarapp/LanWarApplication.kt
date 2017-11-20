package com.mweeksconsulting.lanwarapp

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.persistence.room.Room
import android.content.Context
import android.util.Log
import com.mweeksconsulting.lanwarapp.LanWarApplication.appSingleton.DB
import com.mweeksconsulting.lanwarapp.LanWarApplication.appSingleton.context
import com.mweeksconsulting.lanwarapp.LanWarApplication.appSingleton.repo
import com.mweeksconsulting.lanwarapp.LanWarApplication.appSingleton.sponsorDAO
import com.mweeksconsulting.lanwarapp.LanWarApplication.appSingleton.staffDAO
import com.mweeksconsulting.lanwarapp.data.Repo
import com.mweeksconsulting.lanwarapp.room.LanWarDatabase
import com.mweeksconsulting.lanwarapp.room.SponsorDAO
import com.mweeksconsulting.lanwarapp.sponsor.data.LoadStaffFromDB
import com.mweeksconsulting.lanwarapp.sponsor.data.StaffDAO

class LanWarApplication : Application() {

    object appSingleton{
        lateinit var DB : LanWarDatabase
       lateinit var context : Context
        lateinit var sponsorDAO : SponsorDAO
        lateinit var staffDAO : StaffDAO
        lateinit var repo : Repo


    }

    override fun onCreate() {
        super.onCreate()
        DB = Room.databaseBuilder(this, LanWarDatabase::class.java, "LanWarDataBase").build()
        context=this.applicationContext
        sponsorDAO = DB.sponsorDao
        staffDAO=DB.staffDAO
        repo = Repo()
        Log.i("Lan war app","On Create")


    }

}
