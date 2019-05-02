package com.example.dailyupdate.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.dailyupdate.data.models.GitHubRepo;
import com.example.dailyupdate.repositories.GitHubRepository;

import java.util.List;

public class GitHubViewModel extends ViewModel {

    private GitHubRepository mRepo;
//    private MutableLiveData<Boolean> mIsUpdating = new MutableLiveData<>();

    public GitHubViewModel() {
        mRepo = GitHubRepository.getInstance();
    }

    public LiveData<List<GitHubRepo>> getGitHubRepoList() {
        return mRepo.getGitHubRepoList();
    }

    public void searchGitHubRepoList(String searchKeyword, String sortBy){
        mRepo.searchRepositoriesFromGitHubApi(searchKeyword, sortBy);

    }

    public void searchGitHubRepoListWithOrder(String searchKeyword, String sortBy,
                                              String sortOrder){
        mRepo.searchRepositoriesFromGitHubApiWithOrder(searchKeyword, sortBy, sortOrder);
    }

//    public LiveData<Boolean> getIsUpdating(){
//        return mIsUpdating;
//    }
}
