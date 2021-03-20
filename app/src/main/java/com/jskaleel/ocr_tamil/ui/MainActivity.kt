package com.jskaleel.ocr_tamil.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.jskaleel.ocr_tamil.databinding.ActivityMainBinding
import com.jskaleel.ocr_tamil.utils.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.android.ext.android.inject
import java.io.File

class MainActivity : AppCompatActivity(), CoroutineScope {

    private var localFiles: MutableList<LocalFiles> = mutableListOf()

    private val job = Job()
    override val coroutineContext = Dispatchers.Main + job

    private val preference: AppPreference by inject()
    private val fileUtils: FileUtils by inject()
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.progressBar.visibility = View.GONE
        binding.txtTest1.text = "${preference.getBoolean("is_clicked", false)}"
        binding.txtTest.setOnClickListener {

            preference.put("is_clicked", true)
            binding.txtTest1.text = "${preference.getBoolean("is_clicked", false)}"

            downloadDataSet()
//            startScan()
        }
    }

    private fun startScan() {
        binding.progressBar?.visibility = View.VISIBLE
        localFiles = fileUtils.scanForPDF()
        Log.d("Khaleel", "Size : ${localFiles.size}")
        for (file in localFiles) {
            Log.d("Khaleel", "File : $file")
        }
        binding.progressBar?.visibility = View.GONE
    }


    private fun createFile(context: Context, fileName: String, fileExt: String): File {
        val storageDir = context.getExternalFilesDir(Constants.PATH_OF_TESSERACT_DATA_BEST)?.path
        val file = File("$storageDir/$fileName.$fileExt")
        return storageDir.let { file }
    }

    private fun downloadDataSet() {
        launch {
            binding.progressBar?.visibility = View.VISIBLE
            binding.txtTest1?.text = withContext(Dispatchers.IO) {
                initiateDownload(createFile(baseContext, "இட-ஒதுக்கீடு-உரிமை1", "epub"))
            }
            binding.progressBar?.visibility = View.GONE
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

}