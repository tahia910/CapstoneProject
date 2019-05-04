package com.example.dailyupdate.networking;

import com.example.dailyupdate.utilities.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    private static Retrofit gitHubRetrofit;
    private static Retrofit meetupRetrofit;

    private static GitHubService gitHubService =
            getGitHubRetrofitInstance().create(GitHubService.class);

    public static GitHubService getGitHubService() {
        return gitHubService;
    }

    private static Retrofit getGitHubRetrofitInstance() {
        if (gitHubRetrofit == null) {
            gitHubRetrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(Constants.GITHUB_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return gitHubRetrofit;
    }


    private static MeetupService meetupService =
            getMeetupRetrofitInstance().create(MeetupService.class);

    public static MeetupService getMeetupService() {
        return meetupService;
    }

    private static Retrofit getMeetupRetrofitInstance() {
        if (meetupRetrofit == null) {
            meetupRetrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(Constants.MEETUP_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return meetupRetrofit;
    }
}