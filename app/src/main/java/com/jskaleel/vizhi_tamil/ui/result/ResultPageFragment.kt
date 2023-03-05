package com.jskaleel.vizhi_tamil.ui.result

import android.annotation.SuppressLint
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.googlecode.tesseract.android.TessBaseAPI
import com.jskaleel.vizhi_tamil.R
import com.jskaleel.vizhi_tamil.databinding.FragmentPdfResultBinding
import com.jskaleel.vizhi_tamil.model.PDFPageOut
import com.jskaleel.vizhi_tamil.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
@SuppressLint("SetTextI18n")
class ResultPageFragment : Fragment(R.layout.fragment_pdf_result), TessBaseAPI.ProgressNotifier,
    TextToSpeech.OnInitListener {
    private var pagePosition: Int = -1
    private var totalPage: Int = -1
    private val binding by viewBinding(FragmentPdfResultBinding::bind)

    private var tessScanner: TessScanner? = null

    @Inject
    lateinit var fileUtils: FileUtils

    private val ttsEngine by lazy {
        TextToSpeech(requireActivity(), this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTesseract()
        val position = arguments?.getInt(POSITION, -1) ?: -1
        val size = arguments?.getInt(TOTAL_PAGE, -1) ?: -1
        if (position == -1) {
            binding.txtResult.text = getString(R.string.error_string)
        } else {
            pagePosition = position
            totalPage = size

            binding.txtPage.text =
                "${String.format(getString(R.string.page), (pagePosition + 1))}/$totalPage"
        }
        binding.btnRead.setOnClickListener {
            if (!ttsEngine.isSpeaking) {
                ttsEngine.speak(
                    binding.txtResult.text.toString(),
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    pagePosition.toString()
                )

                binding.btnRead.text = getString(R.string.str_stop)
            } else {
                ttsEngine.stop()
                binding.btnRead.text = getString(R.string.str_read)
            }
        }
    }

    private fun initTesseract() {
        val path = fileUtils.getTessDataPath()?.absolutePath ?: ""
        tessScanner = TessScanner(path, "eng+tam")
    }

    private fun loadResultUI(output: String, accuracy: Int?) {
        binding.progressLayout.visibility = View.GONE
        binding.txtScrollView.visibility = View.VISIBLE
        binding.txtResult.text =
            HtmlCompat.fromHtml(output, HtmlCompat.FROM_HTML_MODE_LEGACY)

        binding.txtAccuracy.text = "${getString(R.string.accuracy)} $accuracy%"
    }

    companion object {
        fun newInstance(position: Int, content: PDFPageOut?, size: Int): ResultPageFragment {
            return ResultPageFragment().apply {
                arguments = bundleOf(
                    POSITION to position,
                    PAGE_CONTENT to content,
                    TOTAL_PAGE to size
                )
            }
        }

        private const val POSITION = "position"
        private const val PAGE_CONTENT = "page_content"
        private const val TOTAL_PAGE = "total_page"
    }

    override fun onResume() {
        super.onResume()
        val content = arguments?.getParcelable<PDFPageOut>(PAGE_CONTENT) as PDFPageOut
        loadResultUI(content.output, content.accuracy)
    }

    override fun onProgressValues(progressValues: TessBaseAPI.ProgressValues?) {
        if (activity != null && isAdded && isVisible) {
            requireActivity().runOnUiThread {
                if (progressValues != null) {
                    when {
                        progressValues.percent > 0 -> {
                            binding.progressLoader.isIndeterminate = false
                            binding.progressLoader.progress = progressValues.percent
                            binding.txtProgress.text = "${progressValues.percent}%"
                        }
                        progressValues.percent >= 100 -> {
                            binding.progressLayout.visibility = View.GONE
                        }
                    }
                } else {
                    binding.progressLayout.visibility = View.GONE
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = ttsEngine.setLanguage(Locale("tam", "IND"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                binding.btnRead.hideView()
            } else {
                binding.btnRead.visible()
            }
        }
    }
}