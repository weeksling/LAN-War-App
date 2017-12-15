package com.mweeksconsulting.lanwarapp.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.mweeksconsulting.lanwarapp.raffle.Item
import com.mweeksconsulting.lanwarapp.raffle.Raffle
import com.mweeksconsulting.lanwarapp.sponsor.Sponsor
import com.mweeksconsulting.lanwarapp.sponsor.data.StaffDAO
import com.mweeksconsulting.lanwarapp.staff.Staff

/**
 * Created by michael on 10/11/17.
 */

@Database (entities = arrayOf(Sponsor::class, Staff::class,Raffle::class, Item::class),version = 3)
abstract class LanWarDatabase :RoomDatabase() {
   abstract val sponsorDao: SponsorDAO
   abstract val staffDAO:StaffDAO
   abstract val raffleDAO:RaffleDAO
   abstract val itemDAO:RaffleItemDAO
}