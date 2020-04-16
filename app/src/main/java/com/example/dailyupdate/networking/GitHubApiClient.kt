package com.example.dailyupdate.networking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dailyupdate.data.models.GitHubRepo
import com.example.dailyupdate.data.models.GitHubResponse
import retrofit2.Call
import retrofit2.Response

/**
 * Inspired by the following article (without the time-out scenario)
 * https://www.nexmobility.com/articles/android-app-using-mvvm-architecture.html
 */
class GitHubApiClient private constructor() {

    companion object {
        val instance = GitHubApiClient()
    }

    /**
     * The actual Retrofit call used in the queryGitHubApi() method below
     */
    private fun getGitHubRetrofitResponse(searchKeyword: String,
                                          sortBy: String,
                                          sortOrder: String): Call<GitHubResponse> {
        return RetrofitInstance.getGitHubService().getGitHubRepoList(searchKeyword,
                sortBy, sortOrder)
    }

    /**
     * Query GitHub API using Retrofit and the Network thread,
     * then update the LiveData object with the response
     */
    fun queryGitHubApiWithOrder(searchKeyword: String, sortBy: String, sortOrder: String): LiveData<List<GitHubRepo>> {
        val data = MutableLiveData<List<GitHubRepo>>()
        AppExecutors.getInstance().networkIO().execute {
            try {
                val response: Response<*> = getGitHubRetrofitResponse(searchKeyword, sortBy,
                        sortOrder).execute()
                if (response.code() == 200) {
                    val gitHubResponse = response.body() as GitHubResponse?
                    val gitHubRepoListResponse = gitHubResponse!!.gitHubRepo
                    data.postValue(gitHubRepoListResponse)
                } else {
                    data.postValue(null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                data.postValue(null)
            }
        }
        return data
    }
}