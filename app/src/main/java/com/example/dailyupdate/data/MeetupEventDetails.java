package com.example.dailyupdate.data;

import com.google.gson.annotations.SerializedName;

public class MeetupEventDetails {

    @SerializedName("name")
    private String eventName;
    @SerializedName("group")
    private MeetupEventGroupName meetupEventGroupName;
    @SerializedName("status")
    private String eventStatus;
    @SerializedName("yes_rsvp_count")
    private int eventAttendees;
    @SerializedName("rsvp_limit")
    private int maximumAttendees;
    @SerializedName("waitlist_count")
    private int waitlistCount;
    @SerializedName("local_date")
    private String eventDate;
    @SerializedName("local_time")
    private String eventTime;
    @SerializedName("venue")
    private MeetupEventLocation locationObject;
    @SerializedName("description")
    private String eventDescription;

    public MeetupEventDetails(String eventName, MeetupEventGroupName meetupEventGroupName,
                              String eventStatus, int eventAttendees, int maximumAttendees,
                              int waitlistCount, String eventDate, String eventTime,
                              MeetupEventLocation locationObject,
                              String eventDescription) {
        this.eventName = eventName;
        this.meetupEventGroupName = meetupEventGroupName;
        this.eventStatus = eventStatus;
        this.eventAttendees = eventAttendees;
        this.maximumAttendees = maximumAttendees;
        this.waitlistCount = waitlistCount;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.locationObject = locationObject;
        this.eventDescription = eventDescription;
    }

    public String getEventName() {
        return eventName;
    }

    public MeetupEventGroupName getEventGroupName() {
        return meetupEventGroupName;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public int getEventAttendees() {
        return eventAttendees;
    }

    public int getMaximumAttendees() {
        return maximumAttendees;
    }

    public int getWaitlistCount() {
        return waitlistCount;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public MeetupEventLocation getLocationObject(){return locationObject;}

    public String getEventDescription() {
        return eventDescription;
    }
}
