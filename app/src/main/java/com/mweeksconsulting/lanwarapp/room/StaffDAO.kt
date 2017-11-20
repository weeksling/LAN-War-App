package com.mweeksconsulting.lanwarapp.sponsor.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.mweeksconsulting.lanwarapp.staff.Staff


/**
 * Created by michael on 16/10/17.
 * Observer
 */
@Dao
interface StaffDAO {
    //insert sponsor if name is the same replace sponsor with new sponsor
    @Insert(onConflict = REPLACE)
    fun saveStaffMember (staff: Staff)
    @Query("DELETE FROM Staff")
    fun deleteSTaff()
    //insert new list of sponsors
    @Insert(onConflict = REPLACE)
    fun saveStaff(staff: ArrayList<Staff>)
    //used to load all sponsors from DB
    @Query("Select * FROM Staff")
    fun loadStaff ():LiveData<List<Staff>>
    //select sponsor dates
    @Query("Select distinct createDate From Staff")
    fun getStaffCreateDate(): Array<String>
}