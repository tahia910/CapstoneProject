package com.example.dailyupdate.data.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "bookmarked_event")
public class MeetupEventDetails {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "event_id")
    @SerializedName("id")
    private String eventId;

    @ColumnInfo(name = "event_name")
    @SerializedName("name")
    private String eventName;

    @Embedded
    @SerializedName("group")
    private MeetupEventGroupName meetupEventGroupName;

    @Ignore
    @SerializedName("status")
    private String eventStatus;
    @Ignore
    @SerializedName("yes_rsvp_count")
    private int eventAttendees;
    @Ignore
    @SerializedName("rsvp_limit")
    private int maximumAttendees;
    @Ignore
    @SerializedName("waitlist_count")
    private int waitlistCount;

    @ColumnInfo(name = "event_date")
    @SerializedName("local_date")
    private String eventDate;

    @ColumnInfo(name = "event_time")
    @SerializedName("local_time")
    private String eventTime;

    @Ignore
    @SerializedName("venue")
    private MeetupEventLocation locationObject;
    @Ignore
    @SerializedName("description")
    private String eventDescription;

    @Ignore
    public MeetupEventDetails(String eventId, String eventName,
                              MeetupEventGroupName meetupEventGroupName, String eventStatus,
                              int eventAttendees, int maximumAttendees, int waitlistCount,
                              String eventDate, String eventTime,
                              MeetupEventLocation locationObject, String eventDescription) {
        this.eventId = eventId;
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

    @Ignore
    public MeetupEventDetails() {
    }

    public MeetupEventDetails(String eventId, String eventName,
                              MeetupEventGroupName meetupEventGroupName, String eventDate,
                              String eventTime) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.meetupEventGroupName = meetupEventGroupName;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
    }

    public String getEventId() {
        return eventId;
    }
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public MeetupEventGroupName getMeetupEventGroupName() {
        return meetupEventGroupName;
    }
    public void setMeetupEventGroupName(MeetupEventGroupName meetupEventGroupName) {
        this.meetupEventGroupName = meetupEventGroupName;
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
    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }
    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public MeetupEventLocation getLocationObject() {
        return locationObject;
    }

    public String getEventDescription() {
        return eventDescription;
    }
}
