package com.jskaleel.ocr_tamil.ui.splash

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jskaleel.ocr_tamil.R
import com.jskaleel.ocr_tamil.databinding.ActivityLauncherBinding
import com.jskaleel.ocr_tamil.model.LoaderState
import com.jskaleel.ocr_tamil.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


@AndroidEntryPoint
class LauncherActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private val binding: ActivityLauncherBinding by lazy {
        ActivityLauncherBinding.inflate(layoutInflater)
    }

    private val activityScope = CoroutineScope(Dispatchers.IO)
    private val launcherViewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(binding.root)

        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (launcherViewModel.isReady.value == true) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        checkPermissionGranted()
                        true
                    } else {
                        false
                    }
                }
            }
        )
        launcherViewModel.delaySplashScreen()
        initObserver()
    }

    private fun initObserver() {
        launcherViewModel.loaderState.observe(this, {
            when (it) {
                LoaderState.INIT -> {
                    binding.txtLoading.text = getString(R.string.initializing)
                }
                LoaderState.VERIFY -> {
                    binding.txtLoading.text = getString(R.string.check_trained_model)
                }
                LoaderState.DOWNLOAD -> {
                    binding.progressLoader.isIndeterminate = false
                    binding.txtLoading.text = getString(R.string.downloading_data)
                }
                LoaderState.READY -> {
                    binding.progressLoader.progress = MAX_PROGRESS
                    binding.txtLoading.text = getString(R.string.verification_completed)
                    launchMainActivity()
                }
                LoaderState.FAILURE -> {
                    binding.progressLoader.visibility = View.INVISIBLE
                    binding.txtLoading.text = getString(R.string.error_string)
                }
            }
        })
        launcherViewModel.downloadProgress.observe(this, {
            it?.let { triple ->
                binding.progressLoader.max = triple.second.toInt()
                binding.progressLoader.progress = triple.first.toInt()
                if (triple.third == "eng") {
                    binding.txtLoading.text =
                        String.format(getString(R.string.downloading_lang_data), "English")
                } else {
                    binding.txtLoading.text =
                        String.format(getString(R.string.downloading_lang_data), "Tamil")
                }
            }
        })
    }

    private fun launchMainActivity() {
        startActivity(MainActivity.newIntent(baseContext))
    }

    private fun checkPermissionGranted() {
        when {
            EasyPermissions.hasPermissions(this, PERMS) -> {
                launcherViewModel.checkBasicSetup(baseContext)
            }
            else -> {
                EasyPermissions.requestPermissions(
                    this,
                    "Vizhi Tamil OCR needs Storage access to perform text conversion",
                    READ_REQUEST_CODE,
                    PERMS
                )
            }
        }
    }

    override fun onDestroy() {
        activityScope.cancel()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        launcherViewModel.checkBasicSetup(baseContext)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.d("Khaleel", "requestCode : $requestCode perms: $perms")
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            checkPermissionGranted()
        }
    }

    companion object {
        const val PERMS = Manifest.permission.READ_EXTERNAL_STORAGE
        const val READ_REQUEST_CODE = 108
        const val MAX_PROGRESS = 100
    }
}