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

    @Query("SELECT * FROM recent_scan WHERE id = :id")
    suspend fun getById(id: Int): RecentScan?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: RecentScan): Long

    @Query("UPDATE recent_scan SET text = :text WHERE id = :id")
    suspend fun updateText(id: Int, text: String)

    @Query("delete from recent_scan where time_stamp =:timeStamp")
    suspend fun deleteScan(timeStamp: Long)

    @Query("DELETE FROM recent_scan WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Int>)
}
