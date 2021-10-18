package com.jskaleel.ocr_tamil.ui.result

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.googlecode.tesseract.android.TessBaseAPI
import com.jskaleel.ocr_tamil.databinding.ActivityResultBinding
import com.jskaleel.ocr_tamil.utils.FileUtils
import com.jskaleel.ocr_tamil.utils.TessScanner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ResultActivity : AppCompatActivity(), TessBaseAPI.ProgressNotifier {
    private val activityScope = CoroutineScope(Dispatchers.IO)

    private val binding: ActivityResultBinding by lazy {
        ActivityResultBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var fileUtils: FileUtils
    private var tessScanner: TessScanner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.progressLayout.visibility = View.VISIBLE
        initTesseract()

        val path = intent.getStringExtra(FILE_PATH_KEY)
        if (path == null) {
            finish()
        } else {
            val bitmap = BitmapFactory.decodeFile(path)
            startOCR(bitmap)
        }
    }

    private fun initTesseract() {
        val path = fileUtils.getTessDataPath()?.absolutePath ?: ""
        tessScanner = TessScanner(path, "eng+tam", this)
    }

    private fun startOCR(bitmap: Bitmap?) {
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

    companion object {
        fun newIntent(context: Context, filePath: String): Intent {
            return Intent(context, ResultActivity::class.java).apply {
                putExtra(FILE_PATH_KEY, filePath)
            }
        }

        private const val FILE_PATH_KEY = "file_path"
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