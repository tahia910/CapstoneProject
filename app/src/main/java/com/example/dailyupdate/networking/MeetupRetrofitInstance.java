package com.example.dailyupdate.networking;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MeetupRetrofitInstance {
    private static Retrofit meetupRetrofit;
    private static final String BASE_URL = "https://api.meetup.com";

    public static Retrofit getMeetupRetrofitInstance() {
        if (meetupRetrofit == null) {
            meetupRetrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return meetupRetrofit;
    }
}
