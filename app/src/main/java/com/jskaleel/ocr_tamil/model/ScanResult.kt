package com.jskaleel.ocr_tamil.model

data class ScanResult(val pageIndex: Int, val text: CharSequence, val accuracy: Int)