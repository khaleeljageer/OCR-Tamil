package com.jskaleel.vizhi_tamil.ui.contrib

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.jskaleel.vizhi_tamil.databinding.LayoutContributorsBinding
import com.jskaleel.vizhi_tamil.model.Contributors
import com.jskaleel.vizhi_tamil.utils.openUrl

class ContributorsAdapter(
    private val contributors: MutableList<Contributors>
) : RecyclerView.Adapter<ContributorsAdapter.ContributorsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContributorsAdapter.ContributorsViewHolder {
        val binding =
            LayoutContributorsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContributorsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ContributorsAdapter.ContributorsViewHolder,
        position: Int
    ) {
        holder.bind(contributors[position])
    }

    override fun getItemCount(): Int = contributors.size

    @SuppressLint("NotifyDataSetChanged")
    fun setContributors(list: List<Contributors>) {
        contributors.clear()
        contributors.addAll(list)
        notifyDataSetChanged()
    }

    inner class ContributorsViewHolder(private val binding: LayoutContributorsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(contributor: Contributors) {
            binding.ivGithubImage.load(contributor.avatar_url) {
                transformations(CircleCropTransformation())
            }
            binding.txtGithubName.text = contributor.login
            binding.root.setOnClickListener {
                openUrl(binding.root.context, contributor.html_url)
            }
        }
    }
}