package com.mweeksconsulting.lanwarapp.raffle

import android.arch.core.internal.FastSafeIterableMap
import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.os.AsyncTask
import com.mweeksconsulting.lanwarapp.LanWarApplication
import com.mweeksconsulting.lanwarapp.LanWarApplication.appSingleton.itemDAO
import com.mweeksconsulting.lanwarapp.LanWarApplication.appSingleton.raffleDAO
import com.mweeksconsulting.lanwarapp.sponsor.Sponsor
import java.util.*

/**
 * Created by michael on 20/11/17.
 */
class LoadRafflesFromDB : AsyncTask<Void, Void, LiveData<List<Raffle>>?>(){


    override fun doInBackground(vararg p0: Void?):  LiveData<List<Raffle>>? {
        return  raffleDAO.loadRaffles()
    }


    internal class SaveItem(val item: Item):AsyncTask<Void,Void,Long>(){
        override fun doInBackground(vararg p0: Void?): Long {
            return itemDAO.saveItem(item)
        }
    }

    internal class SaveItems(val items: Array<Item>):AsyncTask<Void,Void,Array<Long>?>(){
        override fun doInBackground(vararg p0: Void?): Array<Long>? {
         return itemDAO.saveItems(items)
        }
    }

    internal class LoadAllItems:AsyncTask<Void,Void,Array<Item>?>(){
        override fun doInBackground(vararg p0: Void?): Array<Item>? {
            return itemDAO.loadItems()
        }
    }

    internal class LoadRaffleItems(val id:Long):AsyncTask<Void,Void,Array<Item>?>(){
        override fun doInBackground(vararg p0: Void?): Array<Item>? {
            return itemDAO.loadRaffleItems(id)
        }
    }

    internal class CountRaffleItems (val id:Long):AsyncTask<Void,Void,Int>(){
        override fun doInBackground(vararg p0: Void?): Int{
            return itemDAO.countRaffleItems(id)
        }
    }

    internal class CountItems ():AsyncTask<Void,Void,Int>(){
        override fun doInBackground(vararg p0: Void?): Int{
            return itemDAO.countAllItems()
        }
    }



    internal class LoadCreateDates:AsyncTask<Void,Void,Array<String>?>(){
        override fun doInBackground(vararg p0: Void?): Array<String>? {
            return raffleDAO.getCreateDate()
        }
    }

    internal class InsertRaffle(val raffle: Raffle):AsyncTask<Void,Void,Long>(){
        override fun doInBackground(vararg p0: Void?): Long {
            return raffleDAO.saveRaffle(raffle)
        }
    }

    internal class InsertRaffles(val  newRaffleArray: Array<Raffle>):AsyncTask<Void,Void,Array<Long>?>(){
        override fun doInBackground(vararg p0: Void?): Array<Long>? {
             return raffleDAO.saveRaffles(newRaffleArray)
        }
    }

    internal class DeleteRaffles:AsyncTask<Void,Void,Void?>(){
        override fun doInBackground(vararg p0: Void?): Void? {
            raffleDAO.deleteRafflets()
            return null
        }
    }
}