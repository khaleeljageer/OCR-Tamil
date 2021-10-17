package com.jskaleel.ocr_tamil.ui.splash

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.ocr_tamil.model.LoaderState
import com.jskaleel.ocr_tamil.utils.Constants
import com.jskaleel.ocr_tamil.utils.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
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
            initDirectories(context)
            if (!isTrainedDataExist(context)) {
                delay(timeMillis = 300)
                _loaderState.postValue(LoaderState.DOWNLOAD)
                while (isAllLangDownloaded()) {
                    Log.d("LauncherViewModel", "isAllLangDownloaded() : ${isAllLangDownloaded()}")
                    langCode.forEach {
                        if (!it.value) {
                            initiateDownload(context, it)
                        }
                    }
                }

                delay(timeMillis = 300)
                _loaderState.postValue(LoaderState.READY)
            } else {
                delay(timeMillis = 300)
                _loaderState.postValue(LoaderState.READY)
            }
        }
    }

    private fun isAllLangDownloaded(): Boolean {
        return langCode.containsValue(false)
    }

    private fun initiateDownload(context: Context, lang: Map.Entry<String, Boolean>) {
        Log.d("LauncherViewModel", "Lang: ${lang.key}")
        val file = getFilePath(context, lang.key)
        if (file != null) {
            val request =
                Request.Builder()
                    .url(String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_BEST, lang.key))
                    .build()
            Log.d("LauncherViewModel", "URL: ${request.url}")
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
                        Log.d("LauncherViewModel", "Exception : ${e.printStackTrace()}")
                        _loaderState.postValue(LoaderState.FAILURE)
                    } finally {
                        outputStream?.flush()
                        outputStream?.close()
                        buffer?.close()
                    }
                }
            } else {
                Log.d("LauncherViewModel", "Failure : ${response.code}")
                _loaderState.postValue(LoaderState.FAILURE)
            }
        } else {
            _loaderState.postValue(LoaderState.FAILURE)
        }
    }

    private fun publishProgress(current: Long, total: Long, lang: String) {
        _downloadProgress.postValue(Triple(current, total, lang))
    }

    private fun isTrainedDataExist(context: Context): Boolean {
        val rootPath = context.getExternalFilesDir(Constants.TESS_DATA_PATH)
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

    private fun getFilePath(context: Context, lang: String): File? {
        val rootPath = context.getExternalFilesDir(Constants.TESS_DATA_PATH)
        if (rootPath != null) {
            return File(rootPath, String.format(Constants.TESS_DATA_NAME, lang))
        }
        return null
    }

    private fun initDirectories(context: Context) {
        val rootPath = context.getExternalFilesDir(Constants.TESS_DATA_PATH)?.absolutePath
        if (rootPath != null) {
            val rootDir = File(rootPath)
            rootDir.mkdirs()
        }
    }
}