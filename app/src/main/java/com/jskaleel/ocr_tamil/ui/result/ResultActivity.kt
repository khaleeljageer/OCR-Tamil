package com.jskaleel.ocr_tamil.ui.result

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.googlecode.tesseract.android.TessBaseAPI
import com.jskaleel.ocr_tamil.databinding.ActivityResultBinding
import com.jskaleel.ocr_tamil.db.dao.RecentScanDao
import com.jskaleel.ocr_tamil.db.entity.RecentScan
import com.jskaleel.ocr_tamil.model.AppDocFile
import com.jskaleel.ocr_tamil.model.OCRFileType
import com.jskaleel.ocr_tamil.utils.FileUtils
import com.jskaleel.ocr_tamil.utils.TessScanner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ResultActivity : AppCompatActivity(), TessBaseAPI.ProgressNotifier {
    private val activityScope = CoroutineScope(Dispatchers.IO)

    private val binding: ActivityResultBinding by lazy {
        ActivityResultBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var fileUtils: FileUtils

    @Inject
    lateinit var scanDao: RecentScanDao

    private var tessScanner: TessScanner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.progressLayout.visibility = View.VISIBLE
        initTesseract()

        if (intent.hasExtra(OCR_TYPE)) {
            when (intent.getIntExtra(OCR_TYPE, -1)) {
                OCRFileType.IMAGE.ordinal -> {
                    initiateImageProcess()
                }
                OCRFileType.PDF.ordinal -> {
                    initiatePdfProcess()
                }
                else -> {
                    finish()
                }
            }

        } else {
            finish()
        }
    }

    private fun initiatePdfProcess() {
        if (intent.hasExtra(APP_DOC_FILE)) {
            val appDocFile = intent.getParcelableExtra<AppDocFile>(APP_DOC_FILE)
            if (appDocFile != null) {
                activityScope.launch(Dispatchers.IO) {
                    val bitmapList = pdfToBitmap(File(appDocFile.uri.path))
                    Log.d("Khaleel", "bitmapList : ${bitmapList.size}")
                    if (bitmapList.isNotEmpty()) {
                        startOCR(bitmapList[0])
                    }
                }
            } else {
                finish()
            }
        } else {
            finish()
        }
    }


    private fun pdfToBitmap(pdfFile: File): ArrayList<Bitmap> {
        val bitmaps: ArrayList<Bitmap> = ArrayList()
        try {
            val renderer =
                PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY))
            var bitmap: Bitmap
            val pageCount = renderer.pageCount
            for (i in 0 until pageCount) {
                val page = renderer.openPage(i)
                val width = resources.displayMetrics.densityDpi / 72 * page.width
                val height = resources.displayMetrics.densityDpi / 72 * page.height
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bitmaps.add(bitmap)

                page.close()
            }

            // close the renderer
            renderer.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return bitmaps
    }

    private fun initiateImageProcess() {
        val path = intent.getStringExtra(FILE_PATH_KEY)
        val isNewItem = intent.getBooleanExtra(IS_NEW_ITEM_KEY, false)
        if (path == null) {
            finish()
        } else {
            val bitmap = BitmapFactory.decodeFile(path)
            startOCR(bitmap, path, isNewItem)
        }
    }

    private fun initTesseract() {
        val path = fileUtils.getTessDataPath()?.absolutePath ?: ""
        tessScanner = TessScanner(path, "eng+tam", this)
    }

    private fun startOCR(bitmap: Bitmap) {
        activityScope.launch(Dispatchers.IO) {
            tessScanner?.clearLastImage()
            val output = tessScanner?.getTextFromImage(bitmap)
            Log.d("Khaleel", "Accuracy : ${tessScanner?.accuracy()}")
            if (output != null) {
                runOnUiThread {
                    binding.progressLayout.visibility = View.GONE
                    binding.txtAccuracy.text = "Accuracy: ${tessScanner?.accuracy()}%"
                    binding.txtAccuracy.visibility = View.VISIBLE
                    binding.txtOutput.text =
                        HtmlCompat.fromHtml(output, HtmlCompat.FROM_HTML_MODE_LEGACY)
                }
            }
            tessScanner?.stop()
        }
    }

    private fun startOCR(bitmap: Bitmap?, path: String, isNewItem: Boolean) {
        activityScope.launch(Dispatchers.IO) {
            tessScanner?.clearLastImage()
            val output = tessScanner?.getTextFromImage(bitmap)
            Log.d("Khaleel", "Accuracy : ${tessScanner?.accuracy()}")
            if (output != null) {
                runOnUiThread {
                    binding.progressLayout.visibility = View.GONE
                    binding.txtAccuracy.text = "Accuracy: ${tessScanner?.accuracy()}%"
                    binding.txtAccuracy.visibility = View.VISIBLE
                    binding.txtOutput.text =
                        HtmlCompat.fromHtml(output, HtmlCompat.FROM_HTML_MODE_LEGACY)
                }
                if (isNewItem) {
                    updateDb(path)
                }
            }
            tessScanner?.stop()
        }
    }

    private suspend fun updateDb(path: String) {
        val scanData = RecentScan(path, "${System.currentTimeMillis()}", System.currentTimeMillis())
        scanDao.insert(scanData)
    }

    companion object {
        fun newIntent(context: Context, filePath: String, isNewItem: Boolean = true): Intent {
            return Intent(context, ResultActivity::class.java).apply {
                putExtra(FILE_PATH_KEY, filePath)
                putExtra(IS_NEW_ITEM_KEY, isNewItem)
                putExtra(OCR_TYPE, OCRFileType.IMAGE.ordinal)
            }
        }

        fun newIntent(context: Context, appDocFile: AppDocFile): Intent {
            return Intent(context, ResultActivity::class.java).apply {
                putExtra(APP_DOC_FILE, appDocFile)
                putExtra(OCR_TYPE, OCRFileType.PDF.ordinal)
            }
        }

        private const val FILE_PATH_KEY = "file_path"
        private const val IS_NEW_ITEM_KEY = "is_new_item"
        private const val OCR_TYPE = "ocr_type"
        private const val APP_DOC_FILE = "app_doc_file"
    }

    @SuppressLint("SetTextI18n")
    override fun onProgressValues(progressValues: TessBaseAPI.ProgressValues?) {
        runOnUiThread {
            if (progressValues != null) {
                Log.d("Khaleel", "Progress : ${progressValues.percent}")
                when {
                    progressValues.percent > 0 -> {
                        binding.progressLoader.isIndeterminate = false
                        binding.progressLoader.progress = progressValues.percent
                        binding.txtProgress.text = "${progressValues.percent}%"
                    }
                    progressValues.percent >= 100 -> {
                        binding.progressLayout.visibility = View.GONE
                    }
                }
            } else {
                binding.progressLayout.visibility = View.GONE
            }
        }
    }
}