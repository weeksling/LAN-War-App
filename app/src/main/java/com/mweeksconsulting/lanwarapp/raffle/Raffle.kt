package com.mweeksconsulting.lanwarapp.raffle

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
/**
 * Created by michael on 12/12/17.
 * Holds the raffle information
 * This is a table in the DB*
 */
@Entity
class Raffle (val raffleDate:String, val raffleTime:String, val location:String, val createDate: String){
    @PrimaryKey(autoGenerate = true)
    var id : Long =0

    override fun toString(): String {
        return "raffle date: $raffleDate, raffle time: $raffleTime, raffle location: $location, createDate: $createDate"
    }


}

