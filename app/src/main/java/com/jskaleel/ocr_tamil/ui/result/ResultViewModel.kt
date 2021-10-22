package com.jskaleel.ocr_tamil.ui.result

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jskaleel.ocr_tamil.utils.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(val fileUtils: FileUtils) : ViewModel() {

    private val _bitmapList = MutableLiveData<List<Bitmap>>()
    val bitmapList: MutableLiveData<List<Bitmap>> = _bitmapList

    fun convertPdfToBitmap(context: Context, pdfFile: File?) {
        viewModelScope.launch(Dispatchers.IO) {
            val bitmaps: MutableList<Bitmap> = mutableListOf()
            try {
                val renderer =
                    PdfRenderer(
                        ParcelFileDescriptor.open(
                            pdfFile,
                            ParcelFileDescriptor.MODE_READ_ONLY
                        )
                    )
                var bitmap: Bitmap
                val pageCount = renderer.pageCount
                for (i in 0 until pageCount) {
                    val page = renderer.openPage(i)
                    val width = context.resources.displayMetrics.densityDpi / 72 * page.width
                    val height = context.resources.displayMetrics.densityDpi / 72 * page.height
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    bitmaps.add(bitmap)

                    page.close()
                }
                renderer.close()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            _bitmapList.postValue(bitmaps)
        }
    }

    fun getBitmap(position: Int): Bitmap? {
        val list = _bitmapList.value
        return if(list?.isNotEmpty() == true) {
            list[position]
        }else {
            null
        }
    }
}