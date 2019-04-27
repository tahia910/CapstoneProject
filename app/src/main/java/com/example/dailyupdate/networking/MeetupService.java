package com.example.dailyupdate.networking;

import com.example.dailyupdate.data.model.MeetupEventDetails;
import com.example.dailyupdate.data.model.MeetupEventResponse;
import com.example.dailyupdate.data.model.MeetupGroup;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MeetupService {

    // Search for the recommended Meetup tech groups in a given location
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

    // Search for Meetup events in a given location using keyword(s)
    @GET("/find/upcoming_events")
    Call<MeetupEventResponse> getMeetupEventList(@Query("key") String apiKey,
                                                 @Query("location") String location,
                                                 @Query("order") String sortBy,
                                                 @Query("category") int categoryNumber,
                                                 @Query("text") String searchKeyword);

    // Get the details of a given Meetup event using the group ID and the event ID
    @GET("/{urlname}/events/{id}")
    Call<MeetupEventDetails> getMeetupEventDetails(@Path("urlname") String groupId,
                                                   @Path("id") String eventId,
                                                   @Query("key") String apiKey);
}
