package com.jskaleel.vizhi_tamil.ui.contrib

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.jskaleel.vizhi_tamil.R
import com.jskaleel.vizhi_tamil.databinding.ActivityContributorsBinding
import com.jskaleel.vizhi_tamil.model.ContributorsResponse
import com.jskaleel.vizhi_tamil.utils.hideView
import com.jskaleel.vizhi_tamil.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContributorsActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityContributorsBinding.inflate(layoutInflater)
    }

    private val contributorsAdapter by lazy {
        ContributorsAdapter(mutableListOf())
    }

    private val viewModel by viewModels<ContributorsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        initUI()
        initObserver()
    }

    private fun initObserver() {
        viewModel.getContributors(baseContext)
        viewModel.contributorsResponse.observe(this, {
            when (it) {
                is ContributorsResponse.Success -> {
                    contributorsAdapter.setContributors(it.contributors)
                }
                else -> {
                    binding.rvContribList.hideView()
                    binding.progressLoader.hideView()
                    binding.txtMessage.text = getString(R.string.error_string)
                    binding.txtMessage.visible()
                }
            }
        })
        viewModel.showProgress.observe(this, {
            when (it) {
                true -> {
                    binding.rvContribList.hideView()
                    binding.txtMessage.text = getString(R.string.loading)
                    binding.txtMessage.visible()
                    binding.progressLoader.visible()
                }
                else -> {
                    binding.rvContribList.visible()
                    binding.txtMessage.hideView()
                    binding.progressLoader.hideView()
                }
            }
        })
    }

    private fun initUI() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        with(binding.rvContribList) {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(baseContext, 2)
            adapter = contributorsAdapter
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ContributorsActivity::class.java)
        }
    }
}