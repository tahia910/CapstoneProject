package com.example.dailyupdate.repositories

import androidx.lifecycle.LiveData
import com.example.dailyupdate.data.models.GitHubRepo
import com.example.dailyupdate.data.models.GitHubResponse
import com.example.dailyupdate.networking.NetworkBoundResource
import com.example.dailyupdate.networking.RetrofitInstance
import com.example.dailyupdate.utilities.Resource
import kotlinx.coroutines.CoroutineScope

class GitHubRepository {

    fun searchRepositoriesFromGitHubApiWithOrder(searchKeyword: String,
                                                 sortBy: String,
                                                 sortOrder: String,
                                                 scope: CoroutineScope)
            : LiveData<Resource<List<GitHubRepo>>> {
        return object : NetworkBoundResource<List<GitHubRepo>, GitHubResponse>(scope) {
            override fun processResponse(response: GitHubResponse): List<GitHubRepo> {
                return response.gitHubRepo
            }

            override suspend fun createCall(): GitHubResponse {
                return RetrofitInstance.getGitHubService().getGitHubRepoList(searchKeyword,
                        sortBy, sortOrder)
            }

            override fun customErrorMsg(): String? {
                return "Could not retrieve GitHub repositories"
            }

        }.asLiveData()
    }

    companion object {
        val instance = GitHubRepository()
    }
}