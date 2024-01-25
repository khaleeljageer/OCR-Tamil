package com.jskaleel.vizhi_tamil.ui.result.tts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.textview.MaterialTextView
import com.jskaleel.vizhi_tamil.R
import com.jskaleel.vizhi_tamil.databinding.ActivityTtsScreenBinding
import com.jskaleel.vizhi_tamil.utils.tts_engine.tts.builder.TextToSpeechHelper
import com.jskaleel.vizhi_tamil.utils.tts_engine.utils.highlightText
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.Locale

@AndroidEntryPoint
class TTSActivity : AppCompatActivity() {

    private val binding: ActivityTtsScreenBinding by lazy {
        ActivityTtsScreenBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (intent.hasExtra(OCR_TEXT_KEY)) {
            val ocrText = intent.getStringExtra(OCR_TEXT_KEY).orEmpty()
            binding.txtOutput.text = HtmlCompat.fromHtml(ocrText, HtmlCompat.FROM_HTML_MODE_LEGACY)
            initListener()
        } else {
            finish()
        }
    }

    private fun initListener() {
        val ocrText = binding.txtOutput.text.toString()
        binding.ivPlayPause.setOnClickListener {
            speak(
                this@TTSActivity,
                (this@TTSActivity) as LifecycleOwner,
                ocrText,
                binding.txtOutput
            )
        }
    }

    private fun speak(
        activity: Activity,
        owner: LifecycleOwner,
        message: String,
        textView: MaterialTextView
    ) {
        TextToSpeechHelper
            .getInstance(activity)
            .registerLifecycle(owner)
            .setLanguage(Locale("ta"))
            .speak(message)
            .highlight()
            .onHighlight { pair ->
                runOnUiThread {
                    textView.highlightText(
                        start = pair.first,
                        end = pair.second + 1,
                        color = ContextCompat.getColor(
                            activity.baseContext,
                            R.color.app_textBgColor
                        )
                    )
                }
            }
            .onStart {
                Timber.tag("Khaleel").d("onStart : speak: done")
            }
            .onDone {
                Timber.tag("Khaleel").d("onDone : speak: done")
            }
            .onError {
                Timber.tag("Khaleel").d("onError: speak: $it")
            }
    }

    companion object {
        fun newIntent(context: Context, ocrText: String): Intent {
            return Intent(context, TTSActivity::class.java).apply {
                putExtra(OCR_TEXT_KEY, ocrText)
            }
        }

        const val OCR_TEXT_KEY = "ocr_text"
    }
}
