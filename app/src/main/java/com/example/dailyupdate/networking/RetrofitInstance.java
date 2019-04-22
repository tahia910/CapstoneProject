package com.example.dailyupdate.networking;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    private static Retrofit gitHubRetrofit;
    private static Retrofit meetupRetrofit;
    private static final String GITHUB_BASE_URL = "https://api.github.com/";
    private static final String MEETUP_BASE_URL = "https://api.meetup.com";

    public static Retrofit getGitHubRetrofitInstance() {
        if (gitHubRetrofit == null) {
            gitHubRetrofit =
                    new retrofit2.Retrofit.Builder()
                            .baseUrl(GITHUB_BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
        }
        return gitHubRetrofit;
    }

    public static Retrofit getMeetupRetrofitInstance() {
        if (meetupRetrofit == null) {
            meetupRetrofit =
                    new retrofit2.Retrofit.Builder()
                            .baseUrl(MEETUP_BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
        }
        return meetupRetrofit;
    }
}