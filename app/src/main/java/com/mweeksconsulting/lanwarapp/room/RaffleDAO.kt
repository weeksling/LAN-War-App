package com.mweeksconsulting.lanwarapp.room

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.mweeksconsulting.lanwarapp.raffle.Raffle

/**
 * Created by michael on 12/12/17.
 */
@Dao
interface RaffleDAO {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun saveRaffle (raffle: Raffle):Long

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun saveRaffles (raffle: Array<Raffle>):Array<Long>

        @Query("Select * FROM Raffle ORDER BY date(raffleDate), time(raffleTime)")
        fun loadRaffles(): LiveData<List<Raffle>>?

        @Query("Select distinct createDate From Raffle")
        fun getCreateDate(): Array<String>

        @Query("Delete From Raffle")
        fun deleteRafflets()

}