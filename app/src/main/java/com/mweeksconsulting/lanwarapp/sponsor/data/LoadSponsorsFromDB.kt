package com.mweeksconsulting.lanwarapp.sponsor.data

import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import com.mweeksconsulting.lanwarapp.LanWarApplication.appSingleton.sponsorDAO
import com.mweeksconsulting.lanwarapp.sponsor.Sponsor

/**
 * Created by michael on 20/11/17.
 */
class LoadSponsorsFromDB: AsyncTask<Void, Void, LiveData<List<Sponsor>>?>(){

    override fun doInBackground(vararg p0: Void?): LiveData<List<Sponsor>>? {
        return sponsorDAO.loadSponsors()
    }





    internal class LoadSponsorsDate:AsyncTask<Void,Void,Array<String>?>(){
        override fun doInBackground(vararg p0: Void?): Array<String>? {
            return sponsorDAO.getSponsorCreateDate()
        }
    }

    internal class InsertSponsor(val sponsor: Sponsor):AsyncTask<Void,Void,Void?>(){
        override fun doInBackground(vararg p0: Void?): Void? {
             sponsorDAO.saveSponsor(sponsor)
            return null
        }
    }

    internal class InsertSponsors(val  newSponsorArray: ArrayList<Sponsor>):AsyncTask<Void,Void,Void?>(){
        override fun doInBackground(vararg p0: Void?): Void? {
             sponsorDAO.saveSponsors(newSponsorArray)
            return null
        }

    }

    internal class DeleteSponsors:AsyncTask<Void,Void,Void?>(){
        override fun doInBackground(vararg p0: Void?): Void? {
            sponsorDAO.deleteSponsors()
            return null
        }
    }


}