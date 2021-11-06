package com.jskaleel.vizhi_tamil.ui.splash

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jskaleel.vizhi_tamil.R
import com.jskaleel.vizhi_tamil.databinding.ActivityLauncherBinding
import com.jskaleel.vizhi_tamil.model.LoaderState
import com.jskaleel.vizhi_tamil.ui.main.MainActivity
import com.jskaleel.vizhi_tamil.utils.toMB
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber


@AndroidEntryPoint
class LauncherActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private val binding: ActivityLauncherBinding by lazy {
        ActivityLauncherBinding.inflate(layoutInflater)
    }

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

    @SuppressLint("SetTextI18n")
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
                    binding.txtDownloadProgress.visibility = View.GONE
                    binding.txtLoading.text = getString(R.string.error_string)
                }
                LoaderState.NONETWORK -> {
                    binding.progressLoader.visibility = View.INVISIBLE
                    binding.txtDownloadProgress.visibility = View.GONE
                    binding.txtLoading.text = getString(R.string.network_error)
                }
            }
        })
        launcherViewModel.downloadProgress.observe(this, {
            it?.let { triple ->
                binding.progressLoader.max = triple.second.toInt()
                binding.progressLoader.progress = triple.first.toInt()
                binding.txtDownloadProgress.visibility = View.VISIBLE
                binding.txtDownloadProgress.text = "${triple.first.toMB()}/${triple.second.toMB()}"
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
        this@LauncherActivity.finish()
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
        Timber.d("requestCode : $requestCode perms: $perms")
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