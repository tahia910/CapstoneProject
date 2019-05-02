package com.example.dailyupdate.networking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.dailyupdate.data.models.GitHubRepo;
import com.example.dailyupdate.data.models.GitHubResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Inspired by the following article (without the time-out scenario)
 * https://www.nexmobility.com/articles/android-app-using-mvvm-architecture.html
 **/
public class GitHubApiClient {

    private static GitHubApiClient instance;
    private MutableLiveData<List<GitHubRepo>> gitHubRepoList;

    public static GitHubApiClient getInstance() {
        if (instance == null) {
            instance = new GitHubApiClient();
        }
        return instance;
    }

    private GitHubApiClient() {
        gitHubRepoList = new MutableLiveData<>();
    }

    public LiveData<List<GitHubRepo>> getGitHubRepositories() {
        return gitHubRepoList;
    }

    /**
     * The actual Retrofit call used in the queryGitHubApi() method below
     **/
    private Call<GitHubResponse> getGitHubRetrofitResponse(String searchKeyword, String sortBy) {
        return RetrofitInstance.getGitHubService().getGitHubRepoList(searchKeyword, sortBy);
    }

    /**
     * Retrofit call used in the queryGitHubApiWithOrder() method below
     **/
    private Call<GitHubResponse> getGitHubRetrofitResponseWithOrder(String searchKeyword,
                                                                    String sortBy,
                                                                    String sortOrder) {
        return RetrofitInstance.getGitHubService().getGitHubRepoListWithOrder(searchKeyword,
                sortBy, sortOrder);
    }

    /**
     * Query GitHub API using Retrofit and the Network thread,
     * then update the LiveData object with the response
     **/
    public void queryGitHubApi(String searchKeyword, String sortBy) {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = getGitHubRetrofitResponse(searchKeyword, sortBy).execute();
                    // HTTP response status code 200 means that the request was successful
                    if (response.code() == 200) {
                        GitHubResponse gitHubResponse = (GitHubResponse) response.body();
                        List<GitHubRepo> gitHubRepoListResponse = gitHubResponse.getGitHubRepo();
                        // Update the LiveData
                        gitHubRepoList.postValue(gitHubRepoListResponse);
                    } else {
                        // The request was not successful, update the LiveData accordingly
                        gitHubRepoList.postValue(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    gitHubRepoList.postValue(null);
                }
            }
        });
    }

    /**
     * Similar to queryGitHubApi() method above, but with an additional "sortOrder" criteria
     **/
    public void queryGitHubApiWithOrder(String searchKeyword, String sortBy, String sortOrder) {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = getGitHubRetrofitResponseWithOrder(searchKeyword, sortBy,
                            sortOrder).execute();
                    if (response.code() == 200) {
                        GitHubResponse gitHubResponse = (GitHubResponse) response.body();
                        List<GitHubRepo> gitHubRepoListResponse = gitHubResponse.getGitHubRepo();
                        gitHubRepoList.postValue(gitHubRepoListResponse);
                    } else {
                        gitHubRepoList.postValue(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    gitHubRepoList.postValue(null);
                }
            }
        });
    }
}



