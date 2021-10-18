package com.jskaleel.ocr_tamil.ui.main

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.jskaleel.ocr_tamil.R
import com.jskaleel.ocr_tamil.databinding.ActivityMainBinding
import com.jskaleel.ocr_tamil.utils.FileUtils
import com.jskaleel.ocr_tamil.utils.TessScanner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val activityScope = CoroutineScope(Dispatchers.IO)

    @Inject
    lateinit var fileUtils: FileUtils

    @Inject
    lateinit var tessScanner: TessScanner

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    var isFirstTime = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        var bitmap: Bitmap? = null
        binding.btnReset.setOnClickListener {
            tessScanner.stop()
            if (isFirstTime) {
                isFirstTime = false
                bitmap = BitmapFactory.decodeResource(resources, R.drawable.test5)
            } else {
                isFirstTime = true
                bitmap = BitmapFactory.decodeResource(resources, R.drawable.test6)
            }
            startOCR(bitmap)
        }
    }

    private fun startOCR(bitmap: Bitmap?) {
        activityScope.launch(Dispatchers.IO) {
            tessScanner.clearLastImage()
            val output = tessScanner.getTextFromImage(bitmap)
            output.plus("\n\nAccuracy : ${tessScanner.accuracy()}")
            runOnUiThread {
                binding.txtOutput.text =
                    HtmlCompat.fromHtml(output, HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}