package com.example.dailyupdate.repositories;

import androidx.lifecycle.LiveData;

import com.example.dailyupdate.data.model.MeetupEvent;
import com.example.dailyupdate.data.model.MeetupEventDetails;
import com.example.dailyupdate.data.model.MeetupGroup;
import com.example.dailyupdate.networking.MeetupDetailsApiClient;
import com.example.dailyupdate.networking.MeetupEventApiClient;
import com.example.dailyupdate.networking.MeetupGroupApiClient;

import java.util.List;

public class MeetupRepository {

    private static MeetupRepository instance;
    private MeetupGroupApiClient groupApiClient;
    private MeetupEventApiClient eventApiClient;
    private MeetupDetailsApiClient eventDetailsApiClient;

    public static MeetupRepository getInstance() {
        if (instance == null) {
            instance = new MeetupRepository();
        }
        return instance;
    }

    private MeetupRepository() {
        groupApiClient = MeetupGroupApiClient.getInstance();
        eventApiClient = MeetupEventApiClient.getInstance();
        eventDetailsApiClient = MeetupDetailsApiClient.getInstance();
    }

    public LiveData<List<MeetupGroup>> getMeetupGroupList() {
        return groupApiClient.getMeetupGroups();
    }

    public LiveData<List<MeetupEvent>> getMeetupEventList() {
        return eventApiClient.getMeetupEvents();
    }

    public LiveData<MeetupEventDetails> getMeetupEventDetails(){
        return eventDetailsApiClient.getEventDetails();
    }

    public void searchMeetupGroups(String location, int category, int responsePageNumber) {
        groupApiClient.queryMeetupApiForGroups(location, category, responsePageNumber);
    }

    public void searchMeetupEvents(String location, String sortBy, int category,
                                   String searchKeyword) {
        eventApiClient.queryMeetupApiForEvents(location, sortBy, category, searchKeyword);
    }

    public void searchMeetupEventDetails(String groupId, String eventId){
        eventDetailsApiClient.queryMeetupApiForEventDetails(groupId, eventId);
    }
}
