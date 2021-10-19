package com.jskaleel.ocr_tamil.model

import com.jskaleel.ocr_tamil.db.entity.RecentScan

interface RecentScanClickListener {
    fun onItemClick(recentScan: RecentScan)
    fun onDeleteClick(timeStamp: Long)
}