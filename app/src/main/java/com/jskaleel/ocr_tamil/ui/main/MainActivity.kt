package com.jskaleel.ocr_tamil.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.drjacky.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.jskaleel.ocr_tamil.R
import com.jskaleel.ocr_tamil.databinding.ActivityMainBinding
import com.jskaleel.ocr_tamil.ui.SettingsActivity
import com.jskaleel.ocr_tamil.ui.result.ResultActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
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

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val resultData = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    if (resultData != null) {
                        val fileUri = resultData.data
                        if (fileUri != null && fileUri.path != null) {
                            Log.d("Khaleel", "FileURI : ${fileUri.path} ${File(fileUri.path).name}")
                            startActivity(ResultActivity.newIntent(baseContext, fileUri.path!!))
//                            val bitmap = BitmapFactory.decodeFile(fileUri.path)
//                            startOCR(bitmap)
                        } else {
                            Snackbar.make(
                                binding.root,
                                getString(R.string.error_string),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.error_string),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    //                mProfileUri = fileUri
                    //                imgProfile.setImageURI(fileUri)
                }
                ImagePicker.RESULT_ERROR -> {
                    Snackbar.make(
                        binding.root,
                        ImagePicker.getError(resultData),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    Snackbar.make(
                        binding.root,
                        "Selection Cancelled",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

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
}