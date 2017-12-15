package com.mweeksconsulting.lanwarapp.raffle

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.mweeksconsulting.lanwarapp.LanWarApplication
import com.mweeksconsulting.lanwarapp.sponsor.Sponsor
import com.mweeksconsulting.lanwarapp.staff.data_handler.StaffRepo
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.logging.SimpleFormatter
import kotlin.collections.ArrayList

/**
 * Created by michael on 12/12/17.
 * handle the data retreived from raffle repo
 */
class RaffleModel:ViewModel() {
    private var  raffleList : LiveData<List<Raffle>>?= null
    init {
        Log.i("sponsor view model","Init")
        loadRaffles()
    }

    ///get the data from the repo in the background if null
    private fun loadRaffles() {
        Log.i("view model","list is null")
        raffleList = RaffleRepo().getRaffles()
        Log.i("sponsor view model","got sponsors from sponsor REPO")
    }

    //get all raffles
    fun getRaffles():LiveData<List<Raffle>>?{
        return raffleList
    }

    //only want to get the soonest upcoming raffle
    fun getCurrentRaffle():Raffle?{
        val now = Calendar.getInstance()
        val pattern = "dd-MM-yyyy hh:mm a"
        val dateFormat = SimpleDateFormat(pattern)
        val currentDate =  now.time
        val arrList = raffleList?.value

        //loop through all raffles.
        //raffles are sorted by date and time
        //from the database

        arrList?.forEach{
            ele ->
            val input = ele.raffleDate  + ele.raffleTime
            Log.i("Raffle model","input: $input")

            val raffleDate = dateFormat.parse(input)

            Log.i("Raffle model","raffle date ${dateFormat.format(raffleDate)}")
            Log.i("Raffle model","current date ${currentDate}")
            Log.i("Raffle model","raffle date ${dateFormat.format(raffleDate)}")
            Log.i("Raffle model","currrent date ${dateFormat.format(currentDate)}")


            if(currentDate.before(raffleDate)){
                Log.i("Raffle model","return $ele")
                return ele
            }
        }
        Log.i("Raffle model","no raffles return null")

        return null
    }

}