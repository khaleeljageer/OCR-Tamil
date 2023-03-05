package com.jskaleel.vizhi_tamil.ui.result.tts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.jskaleel.vizhi_tamil.R
import com.jskaleel.vizhi_tamil.databinding.ActivityTtsScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.random.Random

@AndroidEntryPoint
class TTSActivity : AppCompatActivity(),
    TextToSpeech.OnInitListener {

    private val binding: ActivityTtsScreenBinding by lazy {
        ActivityTtsScreenBinding.inflate(layoutInflater)
    }
    private val ttsEngine by lazy {
        TextToSpeech(applicationContext, this)
    }
    private val outteranceId = Random.nextInt(1000, 10000).toString()


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
            Timber.d("OCRText : $ocrText")
            if (ttsEngine.isSpeaking) {
                ttsEngine.stop()
                binding.ivPlayPause.setImageResource(R.drawable.ic_round_play_circle)
            } else {
                ttsEngine.speak(
                    ocrText,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    outteranceId
                )

                binding.ivPlayPause.setImageResource(R.drawable.ic_round_pause_circle)
            }
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

    override fun onDestroy() {
        if (ttsEngine.isSpeaking) {
            ttsEngine.stop()
            ttsEngine.shutdown()
        }
        super.onDestroy()
    }

    override fun onInit(status: Int) {
        ttsEngine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(p0: String?) {
                Timber.d("OnStart : $p0")
            }

            override fun onDone(p0: String?) {
                Timber.d("onDone : $p0")
            }

            override fun onError(p0: String?) {
                Timber.d("onError : $p0")
            }

        })
    }
}