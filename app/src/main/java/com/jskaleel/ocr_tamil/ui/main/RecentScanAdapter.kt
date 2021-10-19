package com.jskaleel.ocr_tamil.ui.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.jskaleel.ocr_tamil.databinding.LayoutRecentScanItemBinding
import com.jskaleel.ocr_tamil.db.dao.RecentScanDao
import com.jskaleel.ocr_tamil.db.entity.RecentScan
import com.jskaleel.ocr_tamil.model.RecentScanClickListener
import com.jskaleel.ocr_tamil.utils.Constants
import com.jskaleel.ocr_tamil.utils.toReadableDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File


class RecentScanAdapter(
    private val scanList: MutableList<RecentScan>
) :
    RecyclerView.Adapter<RecentScanAdapter.RecentScanViewHolder>() {

    private var scanItemClickListener: RecentScanClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentScanViewHolder {
        val binding =
            LayoutRecentScanItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val lp = binding.root.layoutParams
        val width = parent.measuredWidth
        lp.height = ((width / Constants.ASPECT_RATIO) / 2.5).toInt()
        binding.root.layoutParams = lp
        return RecentScanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentScanViewHolder, position: Int) {
        holder.bind(scanList[position])
    }

    override fun getItemCount(): Int = scanList.size

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(list: MutableList<RecentScan>) {
        this.scanList.clear()
        this.scanList.addAll(list)
        notifyDataSetChanged()
    }

    fun setListener(listener: RecentScanClickListener) {
        this.scanItemClickListener = listener
    }

    inner class RecentScanViewHolder(private val binding: LayoutRecentScanItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recentScan: RecentScan) {
            binding.ivThumb.load(File(recentScan.filPath))
            binding.txtLabel.text = toReadableDate(recentScan.timeStamp)
            binding.ivDeleteScan.setOnClickListener {
                scanItemClickListener?.onDeleteClick(recentScan.timeStamp)
            }
            binding.root.setOnClickListener {
                scanItemClickListener?.onItemClick(recentScan)
            }
        }
    }
}