package com.jskaleel.ocr_tamil.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jskaleel.ocr_tamil.R
import com.jskaleel.ocr_tamil.utils.Constants
import com.jskaleel.ocr_tamil.utils.LocalFiles
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class MainActivity : AppCompatActivity(), CoroutineScope {

    private var localFiles: MutableList<LocalFiles> = mutableListOf()
    private var txtTest1: TextView? = null
    private var progressBar: ProgressBar? = null

    private val job = Job()
    override val coroutineContext = Dispatchers.Main + job

//    private val preference: AppPreference by inject()
//    private val fileUtils: FileUtils by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txtTest = findViewById<TextView>(R.id.txtTest)
        txtTest1 = findViewById<TextView>(R.id.txtTest1)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar?.visibility = View.GONE
//        txtTest1?.text = "${preference.getBoolean("is_clicked", false)}"
        txtTest.setOnClickListener {

//            preference.put("is_clicked", true)
//            txtTest1?.text = "${preference.getBoolean("is_clicked", false)}"

//            downloadDataSet()
            startScan()
        }


    }

    private fun startScan() {
        progressBar?.visibility = View.VISIBLE
//        localFiles = fileUtils.scanForPDF()
        Log.d("Khaleel", "Size : ${localFiles.size}")
        for (file in localFiles) {
            Log.d("Khaleel", "File : $file")
        }
        progressBar?.visibility = View.GONE
    }


    private fun createFile(context: Context, fileName: String, fileExt: String): File {
        val storageDir = context.getExternalFilesDir(Constants.PATH_OF_TESSERACT_DATA_BEST)?.path
        val file = File("$storageDir/$fileName.$fileExt")
        return storageDir.let { file }
    }

    private fun downloadDataSet() {
        launch {
            progressBar?.visibility = View.VISIBLE
            txtTest1?.text = withContext(Dispatchers.IO) {
                initiateDownload(createFile(baseContext, "eng", "traineddata"))
            }
            progressBar?.visibility = View.GONE
        }
    }

    private fun initiateDownload(files: File): String {
        val client = OkHttpClient()
        val request =
            Request.Builder().url(String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_BEST, "eng"))
                .build()
        val response = client.newCall(request).execute()

        if (response.body != null) {
            val buffer = response.body!!.byteStream()
//            buffer.copyStreamToFile(files)
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
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

}