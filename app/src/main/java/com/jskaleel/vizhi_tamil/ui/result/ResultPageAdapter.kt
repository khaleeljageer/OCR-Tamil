package com.jskaleel.vizhi_tamil.ui.result

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jskaleel.vizhi_tamil.model.PDFPageOut

class ResultPageAdapter(
    fragmentActivity: FragmentActivity,
    private val list: MutableMap<Int, PDFPageOut>
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = list.size
    override fun createFragment(position: Int): Fragment =
        ResultPageFragment.newInstance(position, list[position], list.size)

    override fun getItemId(position: Int): Long = position.toLong()
}
