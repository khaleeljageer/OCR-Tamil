package com.jskaleel.vizhi_tamil.ui.oss

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jskaleel.vizhi_tamil.databinding.ActivityThirdPartyBinding
import com.jskaleel.vizhi_tamil.databinding.LayoutThirdPartiesBinding
import com.jskaleel.vizhi_tamil.model.ThirdParty
import com.jskaleel.vizhi_tamil.utils.Constants
import com.jskaleel.vizhi_tamil.utils.openUrl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThirdPartyActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityThirdPartyBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding.rvThirdParties) {
            this.setHasFixedSize(true)
            this.layoutManager = LinearLayoutManager(baseContext, RecyclerView.VERTICAL, false)
            this.adapter = ThirdPartyAdapter()
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ThirdPartyActivity::class.java)
        }
    }

    class ThirdPartyAdapter : RecyclerView.Adapter<ThirdPartyAdapter.TPViewHolder>() {
        val list = Constants.libraries
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): TPViewHolder {
            return TPViewHolder(
                LayoutThirdPartiesBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }

        override fun onBindViewHolder(holder: TPViewHolder, position: Int) {
            holder.bind(list[position])
        }

        override fun getItemCount(): Int = list.size

        class TPViewHolder(private val binding: LayoutThirdPartiesBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(item: ThirdParty) {
                binding.txtTitle.text = item.name
                binding.txtPageAge.text = item.packAge

                binding.root.setOnClickListener {
                    openUrl(binding.root.context, item.url)
                }
            }
        }
    }
}