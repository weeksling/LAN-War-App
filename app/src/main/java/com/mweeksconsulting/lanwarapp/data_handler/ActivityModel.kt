package com.mweeksconsulting.lanwarapp.data_handler
import android.arch.lifecycle.*
import android.util.Log
import com.mweeksconsulting.lanwarapp.LanWarApplication.appSingleton.repo
import com.mweeksconsulting.lanwarapp.staff.Staff
import com.mweeksconsulting.lanwarapp.data.Repo
import com.mweeksconsulting.lanwarapp.sponsor.Sponsor


/**
 * Created by michael on 31/10/17.
 * this class prepares the data for the sponsor activity
 *
 * class UserViewModel @Inject constructor() {

 */

class ActivityModel : ViewModel(){
    private var  staffList : LiveData<List<Staff>>?= null
    private var  sponsorList : LiveData<List<Sponsor>>?= null

    init {
        Log.i("view model","Init")
        loadSponsors()
        loadStaff()

    }

    ///get the data from the repo in the background if null
    private fun loadSponsors() {
        if (sponsorList == null) {
            Log.i("view model","list is null")
            sponsorList = MutableLiveData<List<Sponsor>>()
            sponsorList = repo.getSponsors()

            Log.i("sponsor view model","got sponsors from sponsor REPO")
        }else{
            Log.i("sponsor view model","list is not null")
        }
    }

    private fun loadStaff() {
        if (staffList == null) {
            Log.i("sponsor view model","list is null")
            staffList = MutableLiveData<List<Staff>>()
            staffList = repo.getStaff()

            Log.i("sponsor view model","got sponsors from sponsor REPO")
        }else{
            Log.i("sponsor view model","list is not null")
        }
    }













    fun getSponsors():LiveData<List<Sponsor>>?{
         return sponsorList
    }

    fun getStaff():LiveData<List<Staff>>?{
        return staffList
    }
}