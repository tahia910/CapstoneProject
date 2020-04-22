package com.example.dailyupdate.networking

import com.example.dailyupdate.data.models.GitHubResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubService {
    @GET("search/repositories")
    suspend fun getGitHubRepoList(@Query("q") searchKeyword: String,
                                  @Query("sort") sortBy: String,
                                  @Query("order") searchOrder: String): GitHubResponse
}