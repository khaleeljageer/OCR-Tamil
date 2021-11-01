package com.jskaleel.ocr_tamil.ui.result

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.ocr_tamil.utils.Constants
import com.jskaleel.ocr_tamil.utils.FileUtils
import com.jskaleel.ocr_tamil.utils.TessScanner
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    val fileUtils: FileUtils
) : ViewModel() {

    private val _pdfResult = MutableLiveData<MutableMap<Int, String>>()
    val pdfResult: MutableLiveData<MutableMap<Int, String>> = _pdfResult

    private val _accuracy = MutableLiveData<Long>()
    val accuracy: MutableLiveData<Long> = _accuracy

    private var pageCount: Int = 0

    fun initiatePdfConversion(pdfFile: File) = viewModelScope.launch(Dispatchers.IO) {
        val renderer =
            PdfRenderer(
                ParcelFileDescriptor.open(
                    pdfFile,
                    ParcelFileDescriptor.MODE_READ_ONLY
                )
            )
        pageCount = renderer.pageCount
        if (pageCount <= Constants.MAX_PAGE_SIZE) {
            val pageOutput = HashMap<Int, String>(pageCount)
            val path = fileUtils.getTessDataPath()?.absolutePath ?: ""
            val ongoingJobs = ArrayList<Deferred<ScanResult>>(Constants.MAX_PARALLEL_JOBS)
            var processedCount = 0
            while (processedCount < pageCount) {
                val job = scanTextFromPdfPageAsync(pdfFile, processedCount, path)
                ongoingJobs += job
                processedCount++
                if (ongoingJobs.size == Constants.MAX_PARALLEL_JOBS || processedCount == pageCount) {
                    val completedJobs = ongoingJobs.awaitAll()
                    for (scanResult in completedJobs) {
                        pageOutput[scanResult.pageIndex] = scanResult.text.toString()
                    }
                    ongoingJobs.clear()
                }
            }
            _pdfResult.postValue(pageOutput)
            Timber.d("jobResult:  $pageOutput")
        }
    }

    private fun scanTextFromPdfPageAsync(
        pdf: File,
        pageIndex: Int,
        scannerPath: String,
    ): Deferred<ScanResult> {

        return viewModelScope.async(Dispatchers.IO) {
            val renderer =
                PdfRenderer(
                    ParcelFileDescriptor.open(
                        pdf,
                        ParcelFileDescriptor.MODE_READ_ONLY
                    )
                )
            Timber.d("pdfPage Initiated: $pageIndex")
            val tessScanner = TessScanner(scannerPath, "eng+tam")
            val page = renderer.openPage(pageIndex)
            val width =
                appContext.resources.displayMetrics.densityDpi / 72 * page.width
            val height =
                appContext.resources.displayMetrics.densityDpi / 72 * page.height
            val bitmap =
                Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            page.render(
                bitmap,
                null,
                null,
                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
            )
            tessScanner.clearLastImage()
            val output = tessScanner.getTextFromImage(bitmap)
            bitmap.recycle()
            page.close()
            tessScanner.stop()
            Timber.d("pdfPage Completed: $pageIndex")
            ScanResult(pageIndex, output)
        }
    }

    private class ScanResult(val pageIndex: Int, val text: CharSequence)
}