package com.jskaleel.ocr_tamil.ui.result

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.jskaleel.ocr_tamil.R
import com.jskaleel.ocr_tamil.databinding.ActivityResultBinding
import com.jskaleel.ocr_tamil.db.dao.RecentScanDao
import com.jskaleel.ocr_tamil.db.entity.RecentScan
import com.jskaleel.ocr_tamil.model.AppDocFile
import com.jskaleel.ocr_tamil.model.OCRFileType
import com.jskaleel.ocr_tamil.utils.CustomPageTransformer
import com.jskaleel.ocr_tamil.utils.FileUtils
import com.jskaleel.ocr_tamil.utils.TessScanner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
open class ResultActivity : AppCompatActivity() {
    private val activityScope = CoroutineScope(Dispatchers.IO)
    private val resultViewModel: ResultViewModel by viewModels()
    private var tessScanner: TessScanner? = null

    private val binding: ActivityResultBinding by lazy {
        ActivityResultBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var fileUtils: FileUtils

    @Inject
    lateinit var scanDao: RecentScanDao

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

    @SuppressLint("SetTextI18n")
    private fun initiatePdfProcess() {
        if (intent.hasExtra(APP_DOC_FILE)) {
            val appDocFile = intent.getParcelableExtra<AppDocFile>(APP_DOC_FILE)
            if (appDocFile != null && appDocFile.uri.path != null) {
                resultViewModel.initiatePdfConversion(File(appDocFile.uri.path!!))
                resultViewModel.pdfResult.observe(this, { list ->
                    if (list.isNotEmpty()) {
                        binding.progressLayout.visibility = View.GONE
                        binding.txtAccuracy.visibility = View.GONE
                        binding.txtScrollView.visibility = View.GONE

                        binding.viewPager.visibility = View.VISIBLE
                        binding.viewPagerNavigator.visibility = View.VISIBLE
                        binding.navigatorShadow.visibility = View.VISIBLE

                        val resultPageAdapter = ResultPageAdapter(this@ResultActivity, list)
                        with(binding.viewPager) {
                            this.offscreenPageLimit = 5
                            this.setPageTransformer(CustomPageTransformer())
                            this.adapter = resultPageAdapter
                        }
                        binding.ivNext.setOnClickListener {
                            with(binding.viewPager) {
                                if (this.currentItem < (list.size - 1)) {
                                    setCurrentItem(this.currentItem + 1, true)
                                }
                            }
                        }

                        binding.ivPrevious.setOnClickListener {
                            with(binding.viewPager) {
                                if (this.currentItem >= 1) {
                                    setCurrentItem(this.currentItem - 1, true)
                                }
                            }
                        }
                        binding.txtFileName.text = appDocFile.name
                    } else {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.error_string),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                })
                resultViewModel.accuracy.observe(this, {
                    binding.txtAccuracy.text = "${getString(R.string.accuracy)} $it%"
                })
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.error_string),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        } else {
            finish()
        }
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
        tessScanner = TessScanner(path, "eng+tam")
    }

    @SuppressLint("SetTextI18n")
    private fun startOCR(bitmap: Bitmap?, path: String, isNewItem: Boolean) {
        activityScope.launch(Dispatchers.IO) {
            tessScanner?.clearLastImage()
            val output = tessScanner?.getTextFromImage(bitmap)
            val accuracy = tessScanner?.accuracy()
            if (output != null) {
                runOnUiThread {
                    binding.progressLayout.visibility = View.GONE
                    binding.viewPager.visibility = View.GONE
                    binding.viewPagerNavigator.visibility = View.GONE
                    binding.navigatorShadow.visibility = View.GONE
                    binding.txtAccuracy.visibility = View.VISIBLE
                    binding.txtScrollView.visibility = View.VISIBLE

                    binding.txtAccuracy.text = "${getString(R.string.accuracy)} $accuracy%"
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun exitByBackKey() {
        MaterialAlertDialogBuilder(this)
            .setCancelable(false)
            .setMessage(getString(R.string.close_page_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                finish()
            }
            .setNegativeButton(
                getString(R.string.no)
            ) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}