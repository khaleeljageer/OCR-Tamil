package com.jskaleel.vizhi_tamil.ui.splash

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.jskaleel.vizhi_tamil.model.Config
import com.jskaleel.vizhi_tamil.model.LoaderState
import com.jskaleel.vizhi_tamil.utils.Constants
import com.jskaleel.vizhi_tamil.utils.FileUtils
import com.jskaleel.vizhi_tamil.utils.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val fileUtils: FileUtils,
    private val okHttpClient: OkHttpClient
) : ViewModel() {
    private var _isReady = MutableLiveData<Boolean>()
    var isReady: MutableLiveData<Boolean> = _isReady

    private var _loaderState = MutableLiveData<LoaderState>()
    var loaderState: MutableLiveData<LoaderState> = _loaderState

    private var _downloadProgress = MutableLiveData<Triple<Long, Long, String>>()
    var downloadProgress: MutableLiveData<Triple<Long, Long, String>> = _downloadProgress

    private val langCode = mutableMapOf("eng" to false, "tam" to false)

    fun delaySplashScreen() {
        viewModelScope.launch {
            delay(timeMillis = 1500)
            _isReady.postValue(true)
        }
    }

    fun checkBasicSetup(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _loaderState.postValue(LoaderState.INIT)
            delay(timeMillis = 500)
            _loaderState.postValue(LoaderState.VERIFY)
            initDirectories()
            if (!isTrainedDataExist()) {
                if (!isNetworkAvailable(context)) {
                    _loaderState.postValue(LoaderState.NONETWORK)
                } else {
                    startDownload()
//                    delay(timeMillis = 300)
//                    _loaderState.postValue(LoaderState.READY)
                }
            } else {
                checkConfig()
//                delay(timeMillis = 300)
//                _loaderState.postValue(LoaderState.READY)
            }
        }
    }

    private suspend fun startDownload() {
        delay(timeMillis = 300)
        _loaderState.postValue(LoaderState.DOWNLOAD)
        while (isAllLangDownloaded()) {
            Timber.d("isAllLangDownloaded() : %s", isAllLangDownloaded())
            langCode.forEach {
                if (!it.value) {
                    initiateDownload(it)
                }
            }
        }
        delay(timeMillis = 300)
        _loaderState.postValue(LoaderState.READY)
    }

    private fun checkConfig() {
        val request =
            Request.Builder()
                .url(Constants.CONFIG_URL)
                .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                _loaderState.postValue(LoaderState.READY)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful && response.body != null) {
                        val body = response.body
                        val config = Gson().fromJson(body!!.string(), Config::class.java)
                        if (config.update_data) {
                            viewModelScope.launch(Dispatchers.IO) {
                                delay(timeMillis = 300)
                                _loaderState.postValue(LoaderState.DOWNLOAD)
                                startDownload()
                            }
                        } else {
                            _loaderState.postValue(LoaderState.READY)
                        }
                    } else {
                        _loaderState.postValue(LoaderState.READY)
                    }
                } catch (e: Exception) {
                    _loaderState.postValue(LoaderState.READY)
                }
            }
        })
    }

    private fun isAllLangDownloaded(): Boolean {
        return langCode.containsValue(false)
    }

    private fun initiateDownload(lang: Map.Entry<String, Boolean>) {
        Timber.d("Lang: " + lang.key)
        val file = getFilePath(lang.key)
        if (file != null) {
            val request =
                Request.Builder()
                    .url(String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_FAST, lang.key))
                    .build()
            Timber.d("URL: " + request.url)
            val response = okHttpClient.newCall(request).execute()
            if (response.code == 200 || response.code == 201) {
                if (response.body != null) {
                    var outputStream: FileOutputStream? = null
                    var buffer: InputStream? = null
                    try {
                        buffer = response.body!!.byteStream()
                        outputStream = FileOutputStream(file)
                        val outBuffer = ByteArray(4 * 1024)
                        var downloaded: Long = 0
                        val target = response.body!!.contentLength()
                        publishProgress(downloaded, target, lang.key)
                        while (true) {
                            val byteCount = buffer.read(outBuffer)
                            if (byteCount < 0) break
                            downloaded += byteCount
                            publishProgress(downloaded, target, lang.key)
                            outputStream.write(outBuffer, 0, byteCount)
                            if (downloaded >= target) {
                                langCode[lang.key] = true
                            }
                        }
                    } catch (e: Exception) {
                        Timber.d("Exception : " + e.printStackTrace())
                        _loaderState.postValue(LoaderState.FAILURE)
                    } finally {
                        outputStream?.flush()
                        outputStream?.close()
                        buffer?.close()
                    }
                }
            } else {
                Timber.d("Failure : " + response.code)
                _loaderState.postValue(LoaderState.FAILURE)
            }
        } else {
            _loaderState.postValue(LoaderState.FAILURE)
        }
    }

    private fun publishProgress(current: Long, total: Long, lang: String) {
        _downloadProgress.postValue(Triple(current, total, lang))
    }

    private fun isTrainedDataExist(): Boolean {
        val rootPath = fileUtils.getAppFileDir()
        if (rootPath != null) {
            langCode.forEach {
                val file =
                    File(rootPath, String.format(Constants.TESS_DATA_NAME, it.key))
                langCode[it.key] = file.exists()
                if (!file.exists()) return false
            }
            return true
        }
        return false
    }

    private fun getFilePath(lang: String): File? {
        val rootPath = fileUtils.getAppFileDir()
        if (rootPath != null) {
            return File(rootPath, String.format(Constants.TESS_DATA_NAME, lang))
        }
        return null
    }

    private fun     initDirectories() {
        val rootPath = fileUtils.getAppFileDir()
        rootPath?.mkdirs()
    }
}