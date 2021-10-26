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
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(val fileUtils: FileUtils) : ViewModel() {

    private val _pdfResult = MutableLiveData<MutableMap<Int, String>>()
    val pdfResult: MutableLiveData<MutableMap<Int, String>> = _pdfResult

    fun initiatePdfConversion(context: Context, pdfFile: File?) {
        pdfFile?.let { file ->
            viewModelScope.launch(Dispatchers.IO) {
                val renderer =
                    PdfRenderer(
                        ParcelFileDescriptor.open(
                            file,
                            ParcelFileDescriptor.MODE_READ_ONLY
                        )
                    )
                val pageCount = renderer.pageCount
                if (pageCount <= Constants.MAX_PAGE_SIZE) {
                    val pageOutput = mutableMapOf<Int, String>()
                    for (i in 0 until pageCount) {
                        pageOutput[i] = ""
                    }
                    val pdfPageChunkedList = pageOutput.keys.chunked(5)
                    Timber.d("pdfPageChunkedList : $pdfPageChunkedList")
                    val path = fileUtils.getTessDataPath()?.absolutePath ?: ""
                    val jobConvert: Deferred<MutableMap<Int, String>> = async {
                        val result = mutableMapOf<Int, String>()
                        for (pdfPageChunk in pdfPageChunkedList) {
                            Timber.d("pdfPageChunk : $pdfPageChunk")
                            val subJob: Deferred<MutableMap<Int, String>> = async {
                                val subResult = mutableMapOf<Int, String>()
                                for (pdfPage in pdfPageChunk) {
                                    Timber.d("pdfPage Initiated: $pdfPage")
                                    val tessScanner = TessScanner(path, "eng+tam")
                                    val page = renderer.openPage(pdfPage)
                                    val width =
                                        context.resources.displayMetrics.densityDpi / 72 * page.width
                                    val height =
                                        context.resources.displayMetrics.densityDpi / 72 * page.height
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
                                    subResult[pdfPage] = output
                                    bitmap.recycle()
                                    page.close()
                                    tessScanner.stop()
                                    Timber.d("pdfPage Completed: $pdfPage")
                                }
                                subResult
                            }
                            val subJobResult: MutableMap<Int, String> = subJob.await()
                            result.putAll(subJobResult)
                        }
                        result
                    }

                    val jobResult = jobConvert.await()
                    _pdfResult.postValue(jobResult)
                    Timber.d("jobResult:  $jobResult")
                }
            }
        }
    }
}