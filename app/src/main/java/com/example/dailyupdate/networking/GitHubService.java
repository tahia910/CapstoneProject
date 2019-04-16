package com.example.dailyupdate.networking;

import com.example.dailyupdate.data.GitHubResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GitHubService {

    @GET("search/repositories")
    Call<GitHubResponse> getGitHubRepoList(@Query("q") String searchKeyword,
                                           @Query("sort") String sortBy);
}
