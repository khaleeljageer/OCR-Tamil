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
import com.jskaleel.vizhi_tamil.utils.FileUtils
import com.jskaleel.vizhi_tamil.utils.TessScanner
import com.jskaleel.vizhi_tamil.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
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
        val enings = ttsEngine.engines
        Timber.tag("Khaleel").d("Engines : $enings")

        binding.btnRead.setOnClickListener {
            ttsEngine.speak(
                binding.txtResult.text.toString(),
                TextToSpeech.QUEUE_FLUSH,
                null,
                Random.nextInt(1000, 10000).toString()
            )
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
        Timber.tag("Khaleel").d(" Status : $status")
    }
}