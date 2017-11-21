package com.mweeksconsulting.lanwarapp.sponsor.data_handler
import android.arch.lifecycle.*
import android.util.Log
import com.mweeksconsulting.lanwarapp.LanWarApplication
import com.mweeksconsulting.lanwarapp.staff.Staff
import com.mweeksconsulting.lanwarapp.sponsor.Sponsor


/**
 * Created by michael on 31/10/17.
 * this class prepares the data for the sponsor activity
 *
 * class UserViewModel @Inject constructor() {

 */

class SponsorModel : ViewModel(){
    private var  sponsorList : LiveData<List<Sponsor>>?= null

    init {
        Log.i("sponsor view model","Init")
        loadSponsors()
    }

    ///get the data from the repo in the background if null
    private fun loadSponsors() {
        if (sponsorList == null) {
            Log.i("view model","list is null")
            sponsorList = MutableLiveData<List<Sponsor>>()
            sponsorList = LanWarApplication.appSingleton.sponsorRepo.getSponsors()

            Log.i("sponsor view model","got sponsors from sponsor REPO")
        }else{
            Log.i("sponsor view model","list is not null")
        }
    }


    fun getSponsors():LiveData<List<Sponsor>>?{
         return sponsorList
    }

}