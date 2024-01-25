package com.jskaleel.vizhi_tamil.ui.splash

import android.annotation.SuppressLint
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


@AndroidEntryPoint
class LauncherActivity : AppCompatActivity() {
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
                        checkBasicSetup()
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
        launcherViewModel.loaderState.observe(this) {
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
        }
        launcherViewModel.downloadProgress.observe(this) {
            it?.let { triple ->
                binding.progressLoader.max = triple.second.toInt()
                binding.progressLoader.progress = triple.first.toInt()
                binding.txtDownloadProgress.visibility = View.VISIBLE
                binding.txtDownloadProgress.text = "${triple.first.toMB()}/${triple.second.toMB()}"
                if (triple.third == "eng") {
                    binding.txtLoading.text =
                        String.format(
                            getString(R.string.downloading_lang_data),
                            getString(R.string.english)
                        )
                } else {
                    binding.txtLoading.text =
                        String.format(
                            getString(R.string.downloading_lang_data),
                            getString(R.string.tamil)
                        )
                }
            }
        }
    }

    private fun launchMainActivity() {
        startActivity(MainActivity.newIntent(baseContext))
        this@LauncherActivity.finish()
    }

    private fun checkBasicSetup() {
        launcherViewModel.checkBasicSetup(baseContext)
    }

    companion object {
        const val MAX_PROGRESS = 100
    }
}