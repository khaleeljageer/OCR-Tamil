package com.jskaleel.ocr_tamil.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.ocr_tamil.db.dao.RecentScanDao
import com.jskaleel.ocr_tamil.db.entity.RecentScan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val recentScanDao: RecentScanDao) : ViewModel() {

    private val _scannedItems = MutableLiveData<MutableList<RecentScan>>()
    val scannedItems: MutableLiveData<MutableList<RecentScan>> = _scannedItems

    fun loadAllScanItems() {
        viewModelScope.launch(Dispatchers.IO) {
            recentScanDao.getAllScan().collect {
                _scannedItems.postValue(it)
            }
        }
    }

    fun insertScan() {
        viewModelScope.launch(Dispatchers.IO) {
//            recentScanDao.insert(
//                RecentScan(
//                    "/storage/emulated/0/Android/data/com.jskaleel.ocr_tamil/files/Pictures/1634622840424_croppedImg.jpg",
//                    "test1",
//                    System.currentTimeMillis()
//                )
//            )
        }
    }

    fun deleteScan(timeStamp: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            recentScanDao.deleteScan(timeStamp)
        }
    }
}