package com.jskaleel.vizhi_tamil.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.GridLayoutManager
import com.anggrayudi.storage.SimpleStorage
import com.anggrayudi.storage.callback.FileCallback
import com.anggrayudi.storage.callback.FilePickerCallback
import com.anggrayudi.storage.file.copyFileTo
import com.github.drjacky.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.jskaleel.vizhi_tamil.R
import com.jskaleel.vizhi_tamil.databinding.ActivityMainBinding
import com.jskaleel.vizhi_tamil.db.entity.RecentScan
import com.jskaleel.vizhi_tamil.model.AppDocFile
import com.jskaleel.vizhi_tamil.model.RecentScanClickListener
import com.jskaleel.vizhi_tamil.ui.SettingsActivity
import com.jskaleel.vizhi_tamil.ui.result.ResultActivity
import com.jskaleel.vizhi_tamil.utils.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), RecentScanClickListener {
    private val activityScope = CoroutineScope(Dispatchers.IO)

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var fileUtils: FileUtils

    private val simpleStorage = SimpleStorage(this)

    @Inject
    lateinit var recentScanAdapter: RecentScanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        initUI()
        initObserver()
    }

    private fun initUI() {
        with(binding.rvRecentList) {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(baseContext, 2)
            adapter = recentScanAdapter
            recentScanAdapter.setListener(this@MainActivity)
        }
        binding.btnCapture.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .cropFreeStyle()
                .createIntentFromDialog { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }
        binding.btnChoosePdf.setOnClickListener {
            simpleStorage.openFilePicker(1111, false, "application/pdf")
        }

        simpleStorage.filePickerCallback = object : FilePickerCallback {
            override fun onFileSelected(requestCode: Int, files: List<DocumentFile>) {
                if (files.isNotEmpty()) {
                    copySelectedFile(files.first())
                }
            }

            override fun onStoragePermissionDenied(requestCode: Int, files: List<DocumentFile>?) {

            }
        }
    }

    private fun copySelectedFile(file: DocumentFile) {
        activityScope.launch(Dispatchers.IO) {
            file.copyFileTo(
                baseContext,
                DocumentFile.fromFile(fileUtils.getPdfFileDir()!!),
                callback = object : FileCallback() {

                    override fun onConflict(
                        destinationFile: DocumentFile,
                        action: FileConflictAction
                    ) {
                        action.confirmResolution(ConflictResolution.REPLACE)
                    }

                    override fun onCompleted(result: Any) {
                        if (result is DocumentFile) {
                            val appDocFile = AppDocFile(result.uri, result.name)
                            startActivity(ResultActivity.newIntent(baseContext, appDocFile))
                        } else {

                        }
                    }
                })
        }
    }

    private fun initObserver() {
        viewModel.loadAllScanItems()

        viewModel.scannedItems.observe(this, {
            if (it != null && it.isNotEmpty()) {
                binding.rvRecentList.visibility = View.VISIBLE
                binding.txtEmptyLabel.visibility = View.GONE
                recentScanAdapter.addItems(it)
            } else {
                binding.rvRecentList.visibility = View.GONE
                binding.txtEmptyLabel.visibility = View.VISIBLE
            }
        })
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
                            Timber.tag("Khaleel")
                                .d("FileURI : ${fileUri.path}")
                            startActivity(ResultActivity.newIntent(baseContext, fileUri.path!!))
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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

    override fun onItemClick(recentScan: RecentScan) {
        startActivity(ResultActivity.newIntent(baseContext, recentScan.filPath, false))
    }

    override fun onDeleteClick(timeStamp: Long) {
        viewModel.deleteScan(timeStamp)
    }
}