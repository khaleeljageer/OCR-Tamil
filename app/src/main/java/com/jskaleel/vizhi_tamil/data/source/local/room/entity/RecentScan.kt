package com.jskaleel.vizhi_tamil.data.source.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_scan")
data class RecentScan(
    @ColumnInfo(name = "file_path")
    val filePath: String,

    @ColumnInfo(name = "time_stamp")
    val timeStamp: Long,

    @ColumnInfo(name = "text")
    val text: String,

    @ColumnInfo(name = "accuracy")
    val accuracy: Int,

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)