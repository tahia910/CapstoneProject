package com.example.dailyupdate.viewmodels

import androidx.lifecycle.*
import com.example.dailyupdate.data.models.GitHubRepo
import com.example.dailyupdate.repositories.GitHubRepository
import com.example.dailyupdate.utilities.Resource

class GitHubViewModel : ViewModel() {
    private val mRepo: GitHubRepository

    init {
        mRepo = GitHubRepository.instance
    }

    private val _gitHubRepoList = MediatorLiveData<Resource<List<GitHubRepo>>>()
    val gitHubRepoList: LiveData<Resource<List<GitHubRepo>>>
        get() = _gitHubRepoList

    fun searchGitHubRepoList(searchKeyword: String,
                             sortBy: String,
                             sortOrder: String = "desc") {
        _gitHubRepoList.addSource(
                mRepo.searchRepositoriesFromGitHubApiWithOrder(
                        searchKeyword,
                        sortBy,
                        sortOrder,
                        viewModelScope),
                _gitHubRepoList::setValue)
    }
}