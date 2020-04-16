package com.example.dailyupdate.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.dailyupdate.data.models.GitHubRepo
import com.example.dailyupdate.repositories.GitHubRepository

class GitHubViewModel : ViewModel() {
    private val mRepo: GitHubRepository

    init {
        mRepo = GitHubRepository.instance
    }

    private val _gitHubRepoList = MediatorLiveData<List<GitHubRepo>>()
    val gitHubRepoList: LiveData<List<GitHubRepo>>
        get() = _gitHubRepoList

    fun searchGitHubRepoList(searchKeyword: String,
                             sortBy: String,
                             sortOrder: String = "desc") {
        _gitHubRepoList.addSource(mRepo.searchRepositoriesFromGitHubApiWithOrder(searchKeyword, sortBy, sortOrder), _gitHubRepoList::setValue)
    }
}