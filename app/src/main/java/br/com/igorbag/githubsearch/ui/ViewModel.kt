package br.com.igorbag.githubsearch.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RepositoryViewModel : ViewModel() {

    lateinit var githubApi: GitHubService

    private val _repositoryList = MutableLiveData<List<Repository>>()
    val repositoryList: MutableLiveData<List<Repository>> = _repositoryList

    fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        githubApi = retrofit.create(GitHubService::class.java)
    }

    fun getAllReposByUserName(userName: String) {

        githubApi.getAllRepositoriesByUser(userName).enqueue(object : Callback<List<Repository>> {
            override fun onResponse(
                call: Call<List<Repository>>,
                response: Response<List<Repository>>
            ) {
                if (response.isSuccessful) {
                    _repositoryList.value = response.body()
                }
            }
            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {}
        })
    }
}