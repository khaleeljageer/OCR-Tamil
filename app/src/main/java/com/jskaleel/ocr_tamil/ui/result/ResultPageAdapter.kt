package com.jskaleel.ocr_tamil.ui.result

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ResultPageAdapter(fragmentActivity: FragmentActivity, private val size: Int) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = size
    override fun createFragment(position: Int): Fragment = ResultPageFragment.newInstance(position)
    override fun getItemId(position: Int): Long = position.toLong()
}
