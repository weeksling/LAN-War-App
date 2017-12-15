package com.mweeksconsulting.lanwarapp.raffle

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
/**
 * Created by michael on 12/12/17.
 */
//we use create date to determine if the xml file has been changed
@Entity
class Raffle (val raffleDate:String, val raffleTime:String, val location:String, val createDate: String){
    @PrimaryKey(autoGenerate = true)
    var id : Long =0

    override fun toString(): String {
        return "raffle date: $raffleDate, raffle time: $raffleTime, raffle location: $location, createDate: $createDate"
    }


}

