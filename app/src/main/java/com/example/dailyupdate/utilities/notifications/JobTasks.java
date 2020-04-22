package com.example.dailyupdate.utilities.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.dailyupdate.R;
import com.example.dailyupdate.networking.RetrofitInstance;
import com.example.dailyupdate.utilities.Constants;

import java.util.ArrayList;
import retrofit2.Response;

//public class JobTasks {
//
//    private static SharedPreferences sharedPref;
//    private static String searchKeyword;
//    private static String sortBy;
//    private static String searchLocation;
//
//    private static void getSharedPreferences(Context context) {
//        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
//        searchKeyword = sharedPref.getString(context.getString(R.string.pref_meetup_edittext_key)
//                , "");
//        sortBy = sharedPref.getString(context.getString(R.string.pref_meetup_sort_key),
//                context.getString(R.string.pref_meetup_sort_default));
//        searchLocation =
//                sharedPref.getString(context.getString(R.string.pref_meetup_location_key), "");
//    }
//
//    public static void searchForNewEvents(Context context, ArrayList<String> latestSearchIds) {
//        getSharedPreferences(context);
//
//        AppExecutors.getInstance().networkIO().execute(() -> {
//            try {
//                Response response = getMeetupRetrofitResponse(searchLocation, sortBy, searchKeyword).execute();
//                if (response.code() == 200) {
//                    MeetupEventResponse eventResponse =
//                            (MeetupEventResponse) response.body();
//                    List<MeetupEvent> eventList = eventResponse.getMeetupEventsList();
//                    // Make a list of the retrieved events IDs
//                    ArrayList<String> newSearchEventIds = new ArrayList<>();
//                    for (int i = 0; i < eventList.size(); i++) {
//                        MeetupEvent currentItem = eventList.get(i);
//                        String eventId = currentItem.getEventId();
//                        newSearchEventIds.add(eventId);
//                    }
//
//                    // Compare last search IDs and current search IDs
//                    // Only the same items will be kept in the current search IDs
//                    // https://www.w3resource.com/java-tutorial/arraylist/arraylist_retainall.php
//                    newSearchEventIds.retainAll(latestSearchIds);
//
//                    // If there is any size difference, then there are new events available
//                    if (newSearchEventIds.size() != latestSearchIds.size()) {
//                        // Create notification to warn the user there are new events
//                        Intent getNotification = new Intent(context, AppService.class);
//                        getNotification.setAction(Constants.ACTION_GET_NOTIFICATION);
//                        PendingIntent getNotificationPendingIntent = PendingIntent.getService(context
//                                , Constants.ACTION_GET_NOTIFICATION_PENDING_INTENT_ID,
//                                getNotification, PendingIntent.FLAG_UPDATE_CURRENT);
//                        try {
//                            getNotificationPendingIntent.send();
//                        } catch (PendingIntent.CanceledException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//    private static Call<MeetupEventResponse> getMeetupRetrofitResponse(String location,
//                                                                       String sortBy,
//                                                                       String searchKeyword) {
//        return RetrofitInstance.getMeetupService().getMeetupEventList(Constants.MEETUP_API_KEY,
//                location, sortBy, Constants.MEETUP_TECH_CATEGORY_NUMBER, searchKeyword);
//    }
//}
