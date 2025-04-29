package com.jskaleel.vizhi_tamil.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jskaleel.vizhi_tamil.data.source.local.room.entity.RecentScan
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentScanDao {
    @Query("SELECT * FROM recent_scan ORDER BY time_stamp DESC")
    fun getAllScan(): Flow<MutableList<RecentScan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: RecentScan)

    @Query("delete from recent_scan where time_stamp =:timeStamp")
    suspend fun deleteScan(timeStamp: Long)
}