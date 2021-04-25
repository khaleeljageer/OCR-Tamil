package com.jskaleel.ocr_tamil.ui

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import com.jskaleel.ocr_tamil.databinding.ActivityMainBinding
import com.jskaleel.ocr_tamil.utils.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.android.ext.android.inject
import java.io.File
import java.net.URI


class MainActivity : AppCompatActivity(), CoroutineScope {

    private var localFiles: MutableList<LocalFiles> = mutableListOf()

    private val job = Job()
    override val coroutineContext = Dispatchers.Main + job

    private val preference: AppPreference by inject()
    private val fileUtils: FileUtils by inject()
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val pickPdf = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            Log.d("Khaleel", "${it.path}")
            showPdfFromUri(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnChooseFile.setOnClickListener {
            pickPdf.launch("application/pdf")
            // showFileChooser()
        }

        binding.progressBar.visibility = View.GONE
        binding.txtTest1.text = "${preference.getBoolean("is_clicked", false)}"
        binding.txtTest.setOnClickListener {

            preference.put("is_clicked", true)
            binding.txtTest1.text = "${preference.getBoolean("is_clicked", false)}"

            downloadDataSet()
//            startScan()
        }
    }

    fun showFileChooser() {
        val chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE)
        chooseFile.type = "application/pdf"
        startActivityForResult(
            Intent.createChooser(chooseFile, "Choose a file"),
            REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        intent?.let {
            if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                val uri: Uri? = intent.data
                if (uri != null) {
                    Log.e("TAG", "Image content URI $uri \n FILE ${uri.path}")
                    showPdfFromUri(uri)
                    pdfToBitmap(getPdfFile())
                    // val filePath = getPathFromUri(uri)
                }
            }
        }
    }

    /**
     * Convert PDF file to Bitmap image
     * TODO : Process should done in asynchronous
     */
    // https://developer.android.com/reference/android/graphics/pdf/PdfRenderer.html
    private fun pdfToBitmap(pdfFile: File): ArrayList<Bitmap> {
        val bitmaps: ArrayList<Bitmap> = ArrayList()
        try {
            val renderer = PdfRenderer(
                ParcelFileDescriptor.open(
                    pdfFile,
                    ParcelFileDescriptor.MODE_READ_ONLY
                )
            )
            var bitmap: Bitmap
            val pageCount = renderer.pageCount
            for (i in 0 until pageCount) {
                val page = renderer.openPage(i)
                val width = resources.displayMetrics.densityDpi / 72 * page.width
                val height = resources.displayMetrics.densityDpi / 72 * page.height
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bitmaps.add(bitmap)

                // close the page
                page.close()
            }

            // close the renderer
            renderer.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        Log.e("TAG", "Image count " + bitmaps.size)
        return bitmaps
    }

    private fun showPdfFromUri(uri: Uri?) {
        binding.pdfView.fromUri(uri)
            .defaultPage(0)
            .spacing(10)
            .load()
    }

    private fun getPathFromUri(uri: Uri): String {
        val id = DocumentsContract.getDocumentId(uri)
        val contentUri = ContentUris.withAppendedId(
            Uri.parse("content://downloads/public_downloads"), id.toLong()
        )

        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(contentUri, projection, null, null, null)
        if (cursor != null) {
            val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        }
        return ""
    }

    private fun startScan() {
        binding.progressBar.visibility = View.VISIBLE
        localFiles = fileUtils.scanForPDF()
        Log.d("Khaleel", "Size : ${localFiles.size}")
        for (file in localFiles) {
            Log.d("Khaleel", "File : $file")
        }
        binding.progressBar.visibility = View.GONE
    }

    // Sample test.pdf file manually copied inside /tesseract4/best/tessdata/
    private fun getPdfFile(): File {
        val storageDir = getExternalFilesDir(Constants.PATH_OF_TESSERACT_DATA_BEST)?.path
        val file = File("$storageDir/test.pdf")
        return storageDir.let { file }
    }

    private fun createFile(context: Context, fileName: String, fileExt: String): File {
        val storageDir = context.getExternalFilesDir(Constants.PATH_OF_TESSERACT_DATA_BEST)?.path
        val file = File("$storageDir/$fileName.$fileExt")
        return storageDir.let { file }
    }

    private fun downloadDataSet() {
        launch {
            binding.progressBar.visibility = View.VISIBLE
            binding.txtTest1.text = withContext(Dispatchers.IO) {
                initiateDownload(createFile(baseContext, "இட-ஒதுக்கீடு-உரிமை1", "epub"))
            }
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun initiateDownload(files: File): String {
        val client = OkHttpClient()
//        val request =
//            Request.Builder().url(String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_BEST, "eng"))
//                .build()

        val request =
            Request.Builder()
                .url("https://freetamilebooks.com/download/%e0%ae%8e%e0%ae%b3%e0%ae%bf%e0%ae%af-%e0%ae%a4%e0%ae%ae%e0%ae%bf%e0%ae%b4%e0%ae%bf%e0%ae%b2%e0%af%8d-machine-learning-epub/")
                .build()
        val response = client.newCall(request).execute()

        if (response.body != null) {
            val buffer = response.body!!.byteStream()
            buffer.copyStreamToFile(files)
        }
        return response.message
    }


    private fun findPdf(dir: File) {
//        val pdfPattern = ".pdf"
//        val listFile: Array<File>? = dir.listFiles()
//        if (listFile != null) {
//            for (i in listFile.indices) {
//                if (listFile[i].isDirectory) {
//                    findPdf(listFile[i])
//                } else {
//                    if (listFile[i].name.endsWith(pdfPattern)) {
//                        pdfFiles.add(listFile[i])
//                    }
//                }
//            }
//        }
    }

    companion object {
        const val REQUEST_CODE = 1001
    }

}