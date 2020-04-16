package com.example.dailyupdate.repositories

import androidx.lifecycle.LiveData
import com.example.dailyupdate.data.models.GitHubRepo
import com.example.dailyupdate.networking.GitHubApiClient

class GitHubRepository private constructor() {
    private val apiClient: GitHubApiClient

    /**
     * Launch the Api query
     */
    fun searchRepositoriesFromGitHubApiWithOrder(searchKeyword: String,
                                                 sortBy: String,
                                                 sortOrder: String): LiveData<List<GitHubRepo>> {
        return apiClient.queryGitHubApiWithOrder(searchKeyword, sortBy, sortOrder)
    }

    companion object {
        val instance = GitHubRepository()
    }

    init {
        apiClient = GitHubApiClient.instance
    }
}