package com.jskaleel.ocr_tamil.ui.result

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
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
class ResultPageFragment : Fragment(R.layout.fragment_pdf_result), TessBaseAPI.ProgressNotifier {
    private var pagePosition: Int = -1
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
        if (position == -1) {
            binding?.txtResult?.text = getString(R.string.error_string)
        } else {
            pagePosition = position
            val bitmap: Bitmap? = resultViewModel.getBitmap(position)
            if (bitmap != null) {
                startOCR(bitmap, binding)
            } else {
                binding?.txtResult?.text = getString(R.string.error_string)
            }
        }
    }

    private fun initTesseract() {
        val path = fileUtils.getTessDataPath()?.absolutePath ?: ""
        tessScanner = TessScanner(path, "eng+tam", this@ResultPageFragment)
    }

    private fun startOCR(bitmap: Bitmap, binding: FragmentPdfResultBinding?) {
        fragmentScope.launch(Dispatchers.IO) {
            tessScanner?.clearLastImage()
            val output = tessScanner?.getTextFromImage(bitmap)
            Log.d("Khaleel", "Accuracy : ${tessScanner?.accuracy()}")
            if (output != null) {
                requireActivity().runOnUiThread {
                    binding?.progressLayout?.visibility = View.GONE
                    binding?.txtScrollView?.visibility = View.VISIBLE
                    binding?.txtResult?.text =
                        HtmlCompat.fromHtml(output, HtmlCompat.FROM_HTML_MODE_LEGACY)
                }
            }
            tessScanner?.stop()
        }
    }

    companion object {
        fun newInstance(position: Int): ResultPageFragment {
            return ResultPageFragment().apply {
                arguments = bundleOf(
                    POSITION to position
                )
            }
        }

        private const val POSITION = "position"
    }

    override fun onStart() {
        super.onStart()
        Log.d("Khaleel", "onStart : $pagePosition")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Khaleel", "onResume : $pagePosition")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Khaleel", "onPause : $pagePosition")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Khaleel", "onStop : $pagePosition")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Khaleel", "onDestroy : $pagePosition")
    }

    @SuppressLint("SetTextI18n")
    override fun onProgressValues(progressValues: TessBaseAPI.ProgressValues?) {
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