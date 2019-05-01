package com.example.dailyupdate.repositories;

import androidx.lifecycle.LiveData;

import com.example.dailyupdate.data.model.GitHubRepo;
import com.example.dailyupdate.networking.GitHubApiClient;

import java.util.List;

public class GitHubRepository {

    private static GitHubRepository instance;
    private GitHubApiClient apiClient;

    public static GitHubRepository getInstance() {
        if (instance == null) {
            instance = new GitHubRepository();
        }
        return instance;
    }

    private GitHubRepository() {
        apiClient = GitHubApiClient.getInstance();
    }

    /**
     * Get the current LiveData object
     **/
    public LiveData<List<GitHubRepo>> getGitHubRepoList() {
        return apiClient.getGitHubRepositories();
    }

    /**
     * Launch the Api query
     **/
    public void searchRepositoriesFromGitHubApi(String searchKeyword, String sortBy) {
        apiClient.queryGitHubApi(searchKeyword, sortBy);
    }

    /**
     * Launch the Api query with an additional "sortOrder" criteria
     **/
    public void searchRepositoriesFromGitHubApiWithOrder(String searchKeyword, String sortBy,
                                                         String sortOrder) {
        apiClient.queryGitHubApiWithOrder(searchKeyword, sortBy, sortOrder);
    }

}
