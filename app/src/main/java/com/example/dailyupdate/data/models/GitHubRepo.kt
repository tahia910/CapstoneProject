package com.example.dailyupdate.data.models

import com.google.gson.annotations.SerializedName

class GitHubRepo(
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String?,
        @SerializedName("html_url") val htmlUrl: String?,
        @SerializedName("description") val description: String?,
        @SerializedName("stargazers_count") val starCount: Int,
        @SerializedName("language") val language: String
)