package com.jskaleel.vizhi_tamil.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jskaleel.vizhi_tamil.data.source.local.room.dao.RecentScanDao
import com.jskaleel.vizhi_tamil.data.source.local.room.entity.RecentScan

@Database(entities = [RecentScan::class], version = 1, exportSchema = false)
abstract class VizhiTamilDatabase : RoomDatabase() {
    abstract fun recentScanDao(): RecentScanDao
}