package com.jskaleel.ocr_tamil.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.github.drjacky.imagepicker.ImagePicker
import com.jskaleel.ocr_tamil.R
import com.jskaleel.ocr_tamil.databinding.ActivityMainBinding
import com.jskaleel.ocr_tamil.ui.SettingsActivity
import com.jskaleel.ocr_tamil.utils.FileUtils
import com.jskaleel.ocr_tamil.utils.TessScanner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSettings -> {
                startActivity(SettingsActivity.newIntent(baseContext))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        var bitmap: Bitmap? = null
//        binding.btnReset.setOnClickListener {
//            tessScanner.stop()
//            if (isFirstTime) {
//                isFirstTime = false
//                bitmap = BitmapFactory.decodeResource(resources, R.drawable.test5)
//            } else {
//                isFirstTime = true
//                bitmap = BitmapFactory.decodeResource(resources, R.drawable.test6)
//            }
//            startOCR(bitmap)
//        }

        binding.btnCapture.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .cropFreeStyle()
                .createIntentFromDialog { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }
        binding.btnChoosePdf.setOnClickListener { }
    }

    private fun startOCR(bitmap: Bitmap?) {
        activityScope.launch(Dispatchers.IO) {
            tessScanner.clearLastImage()
            val output = tessScanner.getTextFromImage(bitmap)
            Log.d("Khaleel", "Accuracy : ${tessScanner.accuracy()}")

            runOnUiThread {
                binding.txtAccuracy.text = "Accuracy: ${tessScanner.accuracy()}%"
                binding.txtAccuracy.visibility = View.VISIBLE
                binding.txtOutput.text =
                    HtmlCompat.fromHtml(output, HtmlCompat.FROM_HTML_MODE_LEGACY)
                tessScanner.stop()
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data!!
                    Log.d("Khaleel", "FileURI : ${fileUri.path} ${File(fileUri.path).name}")
                    val bitmap = BitmapFactory.decodeFile(fileUri.path)
                    startOCR(bitmap)
                    //                mProfileUri = fileUri
                    //                imgProfile.setImageURI(fileUri)
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }
}