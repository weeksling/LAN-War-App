package com.mweeksconsulting.lanwarapp.room

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
interface SponsorDAO {
    //insert sponsor if name is the same replace sponsor with new sponsor
    @Insert(onConflict = REPLACE)
    fun saveSponsor (sponsor: Sponsor)
    @Query("DELETE FROM Sponsor")
    fun deleteSponsors()
    //insert new list of sponsors
    @Insert(onConflict = REPLACE)
    fun saveSponsors (sponsor: ArrayList<Sponsor>)
    //used to load all sponsors from DB
    @Query("Select * FROM Sponsor")
    fun loadSponsors ():LiveData<List<Sponsor>>
    //select sponsor dates
    @Query("Select distinct createDate From Sponsor")
    fun getSponsorCreateDate(): Array<String>
}