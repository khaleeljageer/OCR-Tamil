package com.jskaleel.ocr_tamil.ui.main

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.jskaleel.ocr_tamil.databinding.LayoutRecentScanItemBinding
import com.jskaleel.ocr_tamil.db.entity.RecentScan
import java.io.File

class RecentScanAdapter(private val scanList: MutableList<RecentScan>) :
    RecyclerView.Adapter<RecentScanAdapter.RecentScanViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentScanViewHolder {
        val binding =
            LayoutRecentScanItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentScanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentScanViewHolder, position: Int) {
        holder.bind(scanList[holder.adapterPosition], holder.adapterPosition)
    }

    override fun getItemCount(): Int = scanList.size

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(list: MutableList<RecentScan>) {
        this.scanList.clear()
        this.scanList.addAll(list)
        notifyDataSetChanged()
    }

    fun addItem(item: RecentScan) {
        this.scanList.add(0, item)
        notifyItemChanged(0)
    }

    class RecentScanViewHolder(private val binding: LayoutRecentScanItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recentScan: RecentScan, itemPosition: Int) {
            Log.d("Khaleel", "${recentScan.filPath}")
            binding.ivThumb.load(File(recentScan.filPath))
            binding.txtLabel.text = recentScan.fileName
            binding.ivThumb.setOnClickListener {

            }
        }
    }
}