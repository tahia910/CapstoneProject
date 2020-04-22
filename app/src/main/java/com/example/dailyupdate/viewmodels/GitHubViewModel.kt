package com.example.dailyupdate.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyupdate.data.models.GitHubRepo
import com.example.dailyupdate.repositories.GitHubRepository
import com.example.dailyupdate.utilities.Resource
import kotlinx.coroutines.launch

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
        viewModelScope.launch {
            _gitHubRepoList.addSource(
                    mRepo.searchRepositoriesFromGitHubApiWithOrder(
                            searchKeyword,
                            sortBy,
                            sortOrder,
                            this),
                    _gitHubRepoList::setValue)
        }
    }
}