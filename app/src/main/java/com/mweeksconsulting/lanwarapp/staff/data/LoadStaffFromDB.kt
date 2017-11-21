package com.mweeksconsulting.lanwarapp.sponsor.data

import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import com.mweeksconsulting.lanwarapp.LanWarApplication.appSingleton.staffDAO
import com.mweeksconsulting.lanwarapp.staff.Staff

/**
 * Created by michael on 20/11/17.
 */
class LoadStaffFromDB : AsyncTask<Void, Void, LiveData<List<Staff>>?>(){

    override fun doInBackground(vararg p0: Void?): LiveData<List<Staff>>? {
        return staffDAO.loadStaff()
    }

    internal class LoadStaffDate:AsyncTask<Void,Void,Array<String>?>(){
        override fun doInBackground(vararg p0: Void?): Array<String>? {
            return staffDAO.getStaffCreateDate()
        }
    }

    internal class InsertStaffMember(val staff: Staff):AsyncTask<Void,Void,Void?>(){
        override fun doInBackground(vararg p0: Void?): Void? {
             staffDAO.saveStaffMember(staff)
            return null
        }
    }

    internal class InsertStaff(val  newStaffArray: ArrayList<Staff>):AsyncTask<Void,Void,Void?>(){
        override fun doInBackground(vararg p0: Void?): Void? {
             staffDAO.saveStaff(newStaffArray)
            return null
        }

    }

    internal class DeleteStaff:AsyncTask<Void,Void,Void?>(){
        override fun doInBackground(vararg p0: Void?): Void? {
            staffDAO.deleteSTaff()
            return null
        }
    }


}