package com.jskaleel.ocr_tamil.ui.result

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.googlecode.tesseract.android.TessBaseAPI
import com.jskaleel.ocr_tamil.R
import com.jskaleel.ocr_tamil.databinding.FragmentPdfResultBinding
import com.jskaleel.ocr_tamil.utils.FileUtils
import com.jskaleel.ocr_tamil.utils.TessScanner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("SetTextI18n")
class ResultPageFragment : Fragment(R.layout.fragment_pdf_result), TessBaseAPI.ProgressNotifier {
    private var ocrCompleted: Boolean = false
    private var pagePosition: Int = -1
    private var totalPage: Int = -1
    private var binding: FragmentPdfResultBinding? = null
    private val resultViewModel: ResultViewModel by activityViewModels()
    private val fragmentScope = CoroutineScope(Dispatchers.IO)
    private var tessScanner: TessScanner? = null

    @Inject
    lateinit var fileUtils: FileUtils

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPdfResultBinding.bind(view)
        initTesseract()
        val position = arguments?.getInt(POSITION, -1) ?: -1
        val size = arguments?.getInt(TOTAL_PAGE, -1) ?: -1
        if (position == -1) {
            binding?.txtResult?.text = getString(R.string.error_string)
        } else {
            pagePosition = position
            totalPage = size

            binding?.txtPage?.text =
                "${String.format(getString(R.string.page), (pagePosition + 1))}/$totalPage"
        }
    }

    private fun initTesseract() {
        val path = fileUtils.getTessDataPath()?.absolutePath ?: ""
        tessScanner = TessScanner(path, "eng+tam")
    }

    private fun startOCR(bitmap: Bitmap) {
        fragmentScope.launch(Dispatchers.IO) {
            tessScanner?.clearLastImage()
            val output = tessScanner?.getTextFromImage(bitmap)
            val accuracy = tessScanner?.accuracy()
            if (output != null) {
                fragmentScope.launch(Dispatchers.Main) {
                    loadResultUI(output, accuracy)
                }
            }
            tessScanner?.stop()
            bitmap.recycle()
            ocrCompleted = true
        }
    }

    private fun loadResultUI(output: String, accuracy: Int?) {
        binding?.progressLayout?.visibility = View.GONE
        binding?.txtScrollView?.visibility = View.VISIBLE
        binding?.txtResult?.text =
            HtmlCompat.fromHtml(output, HtmlCompat.FROM_HTML_MODE_LEGACY)

        binding?.txtAccuracy?.text = "${getString(R.string.accuracy)} $accuracy%"
    }

    companion object {
        fun newInstance(position: Int, content: String, size: Int): ResultPageFragment {
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
        val content = arguments?.getString(PAGE_CONTENT, "") ?: ""
        loadResultUI(content, 0)

    }

//    private fun initOCR() {
//        val bitmap: Bitmap? = resultViewModel.getBitmap(pagePosition)
//        if (bitmap != null) {
//            startOCR(bitmap)
//        } else {
//            binding?.txtResult?.text = getString(R.string.error_string)
//        }
//    }

    override fun onProgressValues(progressValues: TessBaseAPI.ProgressValues?) {
        if (activity != null && isAdded && isVisible) {
            requireActivity().runOnUiThread {
                if (progressValues != null) {
                    when {
                        progressValues.percent > 0 -> {
                            binding?.progressLoader?.isIndeterminate = false
                            binding?.progressLoader?.progress = progressValues.percent
                            binding?.txtProgress?.text = "${progressValues.percent}%"
                        }
                        progressValues.percent >= 100 -> {
                            binding?.progressLayout?.visibility = View.GONE
                        }
                    }
                } else {
                    binding?.progressLayout?.visibility = View.GONE
                }
            }
        }
    }
}