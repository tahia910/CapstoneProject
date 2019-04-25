package com.example.dailyupdate.networking;

import com.example.dailyupdate.data.MeetupEventResponse;
import com.example.dailyupdate.data.MeetupGroup;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MeetupService {

    @GET("/find/groups")
    Call<List<MeetupGroup>> getMeetupGroupList(@Query("key") String apiKey,
                                               @Query("location") String location,
                                               @Query("category") int categoryNumber,
                                               @Query("page") int responsePageNumber);

//    @GET("/find/groups")
//    Call<List<MeetupGroup>> getMeetupGroupListWithKeywords(@Query("key") String apiKey,
//                                                           @Query("location") String location,
//                                                           @Query("category") int categoryNumber,
//                                                           @Query("text") String searchKeyword);

    @GET("/find/upcoming_events")
    Call<MeetupEventResponse> getMeetupEventList(@Query("key") String apiKey,
                                                 @Query("location") String location,
                                                 @Query("order") String sortBy,
                                                 @Query("category") int categoryNumber,
                                                 @Query("text") String searchKeyword);
}
