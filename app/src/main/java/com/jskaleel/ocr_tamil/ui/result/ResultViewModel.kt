package com.jskaleel.ocr_tamil.ui.result

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.ocr_tamil.model.PDFPageOut
import com.jskaleel.ocr_tamil.utils.Constants
import com.jskaleel.ocr_tamil.utils.FileUtils
import com.jskaleel.ocr_tamil.utils.TessScanner
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.rendering.PDFRenderer
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

    private val _pdfResult = MutableLiveData<MutableMap<Int, PDFPageOut>>()
    val pdfResult: MutableLiveData<MutableMap<Int, PDFPageOut>> = _pdfResult

    private val _accuracy = MutableLiveData<Long>()
    val accuracy: MutableLiveData<Long> = _accuracy

    fun initiatePdfConversion(pdfFile: File) = viewModelScope.launch(Dispatchers.IO) {
        val renderer =
            PdfRenderer(
                ParcelFileDescriptor.open(
                    pdfFile,
                    ParcelFileDescriptor.MODE_READ_ONLY
                )
            )
        val pageCount = renderer.pageCount
        if (pageCount <= Constants.MAX_PAGE_SIZE) {
            val pageOutput = HashMap<Int, PDFPageOut>(pageCount)
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
                        pageOutput[scanResult.pageIndex] =
                            PDFPageOut(scanResult.text.toString(), scanResult.accuracy)
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
            val pdfDocument: PDDocument = PDDocument.load(pdf)
            val pdfRenderer = PDFRenderer(pdfDocument)

            Timber.d("pdfPage Initiated: $pageIndex")
            val tessScanner = TessScanner(scannerPath, "eng+tam")

            val bitmap = pdfRenderer.renderImageWithDPI(pageIndex, 300f)
            val accuracy = tessScanner.accuracy()
            tessScanner.clearLastImage()
            val output = tessScanner.getTextFromImage(bitmap)
            bitmap.recycle()
            tessScanner.stop()
            Timber.d("pdfPage Completed: $pageIndex")
            ScanResult(pageIndex, output, accuracy)
        }
    }

    private class ScanResult(val pageIndex: Int, val text: CharSequence, val accuracy: Int)
}