package com.example.dailyupdate.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MeetupEventResponse {

    @SerializedName("events")
    private List<MeetupEvent> meetupEventsList;

    public MeetupEventResponse(List<MeetupEvent> meetupEventsList) {
        this.meetupEventsList = meetupEventsList;
    }

    public List<MeetupEvent> getMeetupEventsList() {
        return meetupEventsList;
    }
}
