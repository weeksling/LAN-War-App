package com.mweeksconsulting.lanwarapp.sponsor.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.mweeksconsulting.lanwarapp.sponsor.Sponsor


/**
 * Created by michael on 16/10/17.
 * Observer
 */
@Dao
interface StaffDAO {
    //insert sponsor if name is the same replace sponsor with new sponsor
    @Insert(onConflict = REPLACE)
    fun saveStaffMember (sponsor: Sponsor)

    @Query("DELETE FROM Sponsor")
    fun deleteSTaff()
    //insert new list of sponsors
    @Insert(onConflict = REPLACE)
    fun saveStaff(sponsor: ArrayList<Sponsor>)
    //used to load all sponsors from DB
    @Query("Select * FROM Sponsor")
    fun loadStaff ():LiveData<List<Sponsor>>
    //select sponsor dates
    @Query("Select distinct createDate From Sponsor")
    fun getStaffCreateDate(): Array<String>
}