package com.mweeksconsulting.lanwarapp.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.mweeksconsulting.lanwarapp.raffle.Item
import com.mweeksconsulting.lanwarapp.raffle.Raffle

/**
 * Created by michael on 13/12/17.
 */
@Dao
interface RaffleItemDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveItem(item: Item):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveItems(items: Array<Item>):Array<Long>

    @Query("Select * FROM Item")
    fun loadItems(): Array<Item>

    @Query("Select * FROM Item where raffleID == :raffleID")
    fun loadRaffleItems(raffleID:Long): Array<Item>

    @Query("Select count(*) FROM Item where raffleID == :raffleID")
    fun countRaffleItems(raffleID:Long): Int

    @Query("Select count(*) FROM Item")
    fun countAllItems(): Int

}