package com.jskaleel.vizhi_tamil.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jskaleel.vizhi_tamil.db.entity.RecentScan
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentScanDao {
    @Query("SELECT * FROM recent_scan ORDER BY time_stamp DESC")
    fun getAllScan(): Flow<MutableList<RecentScan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: RecentScan)

    @Query("DELETE FROM recent_scan WHERE time_stamp =:timeStamp")
    suspend fun deleteScan(timeStamp: Long)
}