package com.example.dailyupdate.data.models

import com.google.gson.annotations.SerializedName

class GitHubResponse(
        @SerializedName("total_count") val totalCount: Int,
        @SerializedName("incomplete_result") val incompleteResult: Boolean,
        @SerializedName("items") val gitHubRepo: List<GitHubRepo>)