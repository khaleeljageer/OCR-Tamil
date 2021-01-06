package com.jskaleel.ocr_tamil.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jskaleel.ocr_tamil.R
import com.jskaleel.ocr_tamil.utils.AppPreference
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val preference: AppPreference by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txtTest = findViewById<TextView>(R.id.txtTest)
        val txtTest1 = findViewById<TextView>(R.id.txtTest1)
        txtTest1.text = "${preference.getBoolean("is_clicked", false)}"
        txtTest.setOnClickListener {

            preference.put("is_clicked", true)
            txtTest1.text = "${preference.getBoolean("is_clicked", false)}"
        }
    }

}