package com.jskaleel.ocr_tamil.ui

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jskaleel.ocr_tamil.R
import com.jskaleel.ocr_tamil.utils.AppPreference
import com.jskaleel.ocr_tamil.utils.Constants
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), CoroutineScope {

    private var txtTest1: TextView? = null
    private var progressBar: ProgressBar? = null
    private val job = Job()
    override val coroutineContext = Dispatchers.Main + job

    private val preference: AppPreference by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txtTest = findViewById<TextView>(R.id.txtTest)
        txtTest1 = findViewById<TextView>(R.id.txtTest1)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar?.visibility = View.GONE
        txtTest1?.text = "${preference.getBoolean("is_clicked", false)}"
        txtTest.setOnClickListener {

            preference.put("is_clicked", true)
            txtTest1?.text = "${preference.getBoolean("is_clicked", false)}"

            downloadDataSet()
        }
    }

    private fun downloadDataSet() {
        launch {
            progressBar?.visibility = View.VISIBLE
            txtTest1?.text = withContext(Dispatchers.IO) {
                initiateDownload()
            }
            progressBar?.visibility = View.GONE
        }
    }

    private fun initiateDownload(): String {
        val client = OkHttpClient()
        val request = Request.Builder().url(String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_BEST, "tam")).build()
        val response = client.newCall(request).execute()
        return response.message
    }

}