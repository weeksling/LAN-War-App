package com.mweeksconsulting.lanwarapp.sponsor.data_handler
import android.app.Application
import android.arch.lifecycle.*
import android.util.Log
import com.mweeksconsulting.lanwarapp.sponsor.Sponsor
import com.mweeksconsulting.lanwarapp.sponsor.data.SponsorRepo
import javax.inject.Singleton


/**
 * Created by michael on 31/10/17.
 * this class prepares the data for the sponsor activity
 *
 * class UserViewModel @Inject constructor() {

 */

@Singleton
class SponsorViewModel: ViewModel(){
    private var  sponsorList : LiveData<List<Sponsor>>?= null
    val sponsorRepo: SponsorRepo

    init {

        sponsorRepo = SponsorRepo()
        Log.i("view model","Init")
        Log.i("sponsor view model","sponsors loaded")
        loadSponsors()

    }

    private fun loadSponsors() {
        if (sponsorList == null) {

            Log.i("sponsor view model","list is null")
            sponsorList = MutableLiveData<List<Sponsor>>()
            sponsorList = sponsorRepo.getSponsors()

            Log.i("sponsor view model","got sponsors from sponsor REPO")
        }else{
            Log.i("sponsor view model","list is not null")
        }
    }

    fun getSponsors():LiveData<List<Sponsor>>?{
         return sponsorList
    }




}