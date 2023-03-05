package com.jskaleel.vizhi_tamil.ui.result.tts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.google.android.material.snackbar.Snackbar
import com.jskaleel.vizhi_tamil.databinding.ActivityTtsScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class TTSActivity : AppCompatActivity(),
    TextToSpeech.OnInitListener {

    private val binding: ActivityTtsScreenBinding by lazy {
        ActivityTtsScreenBinding.inflate(layoutInflater)
    }
    private val ttsEngine by lazy {
        TextToSpeech(applicationContext, this)
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
        binding.ivPlayPause.setOnClickListener {
            val ocrText = binding.txtOutput.text.toString()
            Timber.d(ocrText)
            ttsEngine.speak(
                ocrText,
                TextToSpeech.QUEUE_FLUSH,
                null,
                UTTERANCE_ID.toString()
            )
        }
    }

    companion object {
        fun newIntent(context: Context, ocrText: String): Intent {
            return Intent(context, TTSActivity::class.java).apply {
                putExtra(OCR_TEXT_KEY, ocrText)
            }
        }

        const val OCR_TEXT_KEY = "ocr_text"
        const val UTTERANCE_ID = 1000
    }

    override fun onDestroy() {
        if (ttsEngine.isSpeaking) {
            ttsEngine.stop()
            ttsEngine.shutdown()
        }
        super.onDestroy()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = ttsEngine.setLanguage(Locale("tam", "IND"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Snackbar.make(
                    binding.root,
                    "Language not supported",
                    Snackbar.LENGTH_INDEFINITE
                ).show()
            }
        }
    }
}