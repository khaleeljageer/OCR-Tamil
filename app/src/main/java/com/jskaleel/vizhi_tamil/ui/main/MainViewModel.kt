package com.jskaleel.vizhi_tamil.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.vizhi_tamil.db.dao.RecentScanDao
import com.jskaleel.vizhi_tamil.db.entity.RecentScan
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

    fun deleteScan(timeStamp: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            recentScanDao.deleteScan(timeStamp)
        }
    }
}