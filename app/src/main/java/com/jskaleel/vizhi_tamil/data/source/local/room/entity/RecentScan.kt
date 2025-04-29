package com.jskaleel.vizhi_tamil.data.source.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_scan")
data class RecentScan(
    @ColumnInfo(name = "file_path")
    val filPath: String,

    @ColumnInfo(name = "file_name")
    val fileName: String,

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "time_stamp")
    val timeStamp: Long
)