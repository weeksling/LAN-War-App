package com.mweeksconsulting.lanwarapp.staff.data_handler
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

class StaffModel : ViewModel(){
    private var  staffList : LiveData<List<Staff>>?= null

    init {
        Log.i("view model","Init")
        loadStaff()

    }

    private fun loadStaff() {
        if (staffList == null) {
            Log.i("sponsor view model","list is null")
            staffList = MutableLiveData<List<Staff>>()
            staffList = LanWarApplication.appSingleton.staffRepo.getStaff()

            Log.i("sponsor view model","got sponsors from sponsor REPO")
        }else{
            Log.i("sponsor view model","list is not null")
        }
    }

    fun getStaff():LiveData<List<Staff>>?{
        return staffList
    }
}