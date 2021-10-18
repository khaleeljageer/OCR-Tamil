package com.jskaleel.ocr_tamil.ui.result

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jskaleel.ocr_tamil.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private val binding: ActivityResultBinding by lazy {
        ActivityResultBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}