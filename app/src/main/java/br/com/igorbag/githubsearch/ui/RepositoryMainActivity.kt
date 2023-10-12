package br.com.igorbag.githubsearch.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.databinding.ActivityMainBinding
import br.com.igorbag.githubsearch.ui.viewmodel.RepositoryViewModel
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter


class RepositoryMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPref: SharedPreferences
    private lateinit var repositoryAdapter: RepositoryAdapter
    private lateinit var repositoryViewModel: RepositoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
    }

    private fun setup() {
        repositoryViewModel = ViewModelProvider(this).get(RepositoryViewModel::class.java)
        repositoryViewModel.setupRetrofit()
        sharedPref = getSharedPreferences("userName", Context.MODE_PRIVATE)
    }

    override fun onStart() {
        super.onStart()
        showUserName()
        setupClickListeners()
        observeViewModel()
        setupAdapter()
        showRepositories()
    }

    private fun setupClickListeners() {
        binding.btnConfirmar.setOnClickListener {
            saveUserLocal()
            showRepositories()
        }
    }

    private fun showRepositories() {
        val userName = sharedPref.getString("userName", "")
        if (!userName.isNullOrEmpty()) {
            repositoryViewModel.getAllReposByUserName(userName)
        }
    }

    private fun saveUserLocal() {
        val userName = binding.etUserName.text.toString()

        if (userName.isNotEmpty()) {
            with (sharedPref.edit()) {
                putString("userName", userName)
                apply()
            }
        } else {
            binding.etUserName.error = getString(R.string.empty_username)
        }
    }

    private fun showUserName() {
        val userNameRecovered = sharedPref.getString("userName", "")
        if (!userNameRecovered.isNullOrEmpty()) {
            binding.etUserName.setText(userNameRecovered)
        }
    }

    private fun observeViewModel() {
        repositoryViewModel.repositoryList.observe(this) { repositoryList ->
            repositoryAdapter.submitList(repositoryList)
        }
    }

    private fun setupAdapter() {
        repositoryAdapter = RepositoryAdapter()
        with(binding.rvListRepositories) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(false)
            adapter = repositoryAdapter
        }

        repositoryAdapter.btnShareLister = {
            repositoryViewModel.shareRepositoryLink(this, it.htmlUrl)
        }

        repositoryAdapter.bodyItemList = {
            repositoryViewModel.openBrowser(this, it.htmlUrl)
        }
    }
}