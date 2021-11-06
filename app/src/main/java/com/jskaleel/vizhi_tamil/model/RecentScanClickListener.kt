package com.jskaleel.vizhi_tamil.model

import com.jskaleel.vizhi_tamil.db.entity.RecentScan

interface RecentScanClickListener {
    fun onItemClick(recentScan: RecentScan)
    fun onDeleteClick(timeStamp: Long)
}