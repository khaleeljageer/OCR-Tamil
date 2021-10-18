package com.jskaleel.ocr_tamil.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.jskaleel.ocr_tamil.BuildConfig
import com.jskaleel.ocr_tamil.R
import com.jskaleel.ocr_tamil.databinding.ActivitySettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    private val binding: ActivitySettingsBinding by lazy {
        ActivitySettingsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.txtAppVersion.text =
            String.format(getString(R.string.version), BuildConfig.VERSION_NAME)

        binding.txtKaniyamDesc.text = HtmlCompat.fromHtml(
            getString(R.string.kaniyam_foundation_desc),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        binding.txtVglugDesc.text = HtmlCompat.fromHtml(
            getString(R.string.vglug_foundation_desc),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }


    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }
}