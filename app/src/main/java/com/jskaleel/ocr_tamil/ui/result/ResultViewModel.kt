package com.jskaleel.ocr_tamil.ui.result

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.ocr_tamil.R
import com.jskaleel.ocr_tamil.model.ConverterResult
import com.jskaleel.ocr_tamil.model.PDFPageOut
import com.jskaleel.ocr_tamil.model.ScanResult
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

    private val _processedPages = MutableLiveData<Int>()
    val processedPages: MutableLiveData<Int> = _processedPages

    private val _errorMessage = MutableLiveData<ConverterResult>()
    val errorMessage: MutableLiveData<ConverterResult> = _errorMessage

    private var processedPage = 0
    var pageCount: Int = 0

    fun initiatePdfConversion(context: Context, pdfFile: File) =
        viewModelScope.launch(Dispatchers.IO) {
            val renderer =
                PdfRenderer(
                    ParcelFileDescriptor.open(
                        pdfFile,
                        ParcelFileDescriptor.MODE_READ_ONLY
                    )
                )
            pageCount = renderer.pageCount
            if (pageCount <= Constants.MAX_PAGE_SIZE) {
                publishProcessedPage()
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
                    Timber.tag("Khaleel").d("ProcessedCount: $processedCount")
                }
                _pdfResult.postValue(pageOutput)
                Timber.d("jobResult:  $pageOutput")
            } else {
                _errorMessage.postValue(
                    ConverterResult.MaxPageError(
                        String.format(
                            context.getString(R.string.exceed_maximum_page),
                            Constants.MAX_PAGE_SIZE
                        )
                    )
                )
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

            Timber.tag("Khaleel").d("pdfPage Initiated: $pageIndex")
            val tessScanner = TessScanner(scannerPath, "eng+tam")
            val bitmap = pdfRenderer.renderImageWithDPI(pageIndex, 300f)
            tessScanner.clearLastImage()
            val output = tessScanner.getTextFromImage(bitmap)
            val accuracy = tessScanner.accuracy()
            bitmap.recycle()
            tessScanner.stop()
            Timber.tag("Khaleel").d("pdfPage Completed: $pageIndex Accuracy : $accuracy")
            publishProcessedPage()
            ScanResult(pageIndex, output, accuracy)
        }
    }

    private fun publishProcessedPage() {
        _processedPages.postValue(processedPage++)
    }
}