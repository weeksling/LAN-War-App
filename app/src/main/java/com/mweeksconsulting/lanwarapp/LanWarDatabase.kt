package com.mweeksconsulting.lanwarapp

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.mweeksconsulting.lanwarapp.sponsor.Sponsor
import com.mweeksconsulting.lanwarapp.sponsor.data.SponsorDAO
import com.mweeksconsulting.lanwarapp.sponsor.data.StaffDAO

/**
 * Created by michael on 10/11/17.
 */
@Database (entities = arrayOf(Sponsor::class),version = 1)
abstract class LanWarDatabase :RoomDatabase() {
   abstract val sponsorDao: SponsorDAO
   abstract val staffDAO:StaffDAO
}