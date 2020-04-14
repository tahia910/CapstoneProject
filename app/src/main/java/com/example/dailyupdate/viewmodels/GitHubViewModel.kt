package com.example.dailyupdate.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.dailyupdate.data.models.GitHubRepo
import com.example.dailyupdate.repositories.GitHubRepository

class GitHubViewModel : ViewModel() {
    private val mRepo: GitHubRepository

    init {
        mRepo = GitHubRepository.getInstance()
    }

    val gitHubRepoList: LiveData<List<GitHubRepo>>
        get() = mRepo.gitHubRepoList

    fun searchGitHubRepoList(searchKeyword: String?, sortBy: String?) =
            mRepo.searchRepositoriesFromGitHubApi(searchKeyword, sortBy)

    fun searchGitHubRepoListWithOrder(searchKeyword: String?,
                                      sortBy: String?,
                                      sortOrder: String?) =
            mRepo.searchRepositoriesFromGitHubApiWithOrder(searchKeyword, sortBy, sortOrder)

}