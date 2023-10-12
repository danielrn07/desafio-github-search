package br.com.igorbag.githubsearch.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.databinding.ActivityMainBinding
import br.com.igorbag.githubsearch.domain.Repository
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RepositoryMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPref: SharedPreferences
    private lateinit var repositoryAdapter: RepositoryAdapter
    private lateinit var repositoryViewModel: RepositoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repositoryViewModel = ViewModelProvider(this).get(RepositoryViewModel::class.java)
        repositoryViewModel.setupRetrofit()

        sharedPref = getSharedPreferences("userName", Context.MODE_PRIVATE)

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
        val userName = binding.etNomeUsuario.text.toString()

        if (userName.isNotEmpty()) {
            with (sharedPref.edit()) {
                putString("userName", userName)
                apply()
            }
        } else {
            binding.etNomeUsuario.error = getString(R.string.empty_username)
        }
    }

    private fun showUserName() {
        val userNameRecovered = sharedPref.getString("userName", "")
        if (!userNameRecovered.isNullOrEmpty()) {
            binding.etNomeUsuario.setText(userNameRecovered)
        }
    }

    private fun observeViewModel() {
        repositoryViewModel.repositoryList.observe(this) { repositoryList ->
            repositoryAdapter.submitList(repositoryList)
        }
    }

    fun setupAdapter() {
        repositoryAdapter = RepositoryAdapter()
        with(binding.rvListRepositories) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(false)
            adapter = repositoryAdapter
        }
    }


    // Metodo responsavel por compartilhar o link do repositorio selecionado
    // @Todo 11 - Colocar esse metodo no click do share item do adapter
    fun shareRepositoryLink(urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    // Metodo responsavel por abrir o browser com o link informado do repositorio

    // @Todo 12 - Colocar esse metodo no click item do adapter
    fun openBrowser(urlRepository: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )

    }

}