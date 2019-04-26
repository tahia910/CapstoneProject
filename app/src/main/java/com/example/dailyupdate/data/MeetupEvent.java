package com.example.dailyupdate.data;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MeetupEvent implements Parcelable {

    @SerializedName("id")
    private String eventId;
    @SerializedName("name")
    private String eventName;
    @SerializedName("status")
    private String eventStatus;
    @SerializedName("local_date")
    private String eventDate;
    @SerializedName("local_time")
    private String eventTime;
    @SerializedName("group")
    private MeetupEventGroupName groupName;
    @SerializedName("link")
    private String eventUrl;
    @SerializedName("yes_rsvp_count")
    private int attendeesCount;

    public MeetupEvent(String eventId, String eventName, String eventStatus, String eventDate,
                       String eventTime,
                       MeetupEventGroupName groupName, String eventUrl, int attendeesCount) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventStatus = eventStatus;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.groupName = groupName;
        this.eventUrl = eventUrl;
        this.attendeesCount = attendeesCount;
    }

    @SuppressLint("NewApi")
    private MeetupEvent(Parcel in) {
        eventId = in.readString();
        eventName = in.readString();
        eventStatus = in.readString();
        eventDate = in.readString();
        eventTime = in.readString();
        groupName = in.readTypedObject(MeetupEventGroupName.CREATOR);
        eventUrl = in.readString();
        attendeesCount = in.readInt();
    }

    public static final Creator<MeetupEvent> CREATOR = new Creator<MeetupEvent>() {
        @Override
        public MeetupEvent createFromParcel(Parcel in) {
            return new MeetupEvent(in);
        }

        @Override
        public MeetupEvent[] newArray(int size) {
            return new MeetupEvent[size];
        }
    };

    public String getEventId(){return eventId;}

    public String getEventName() {
        return eventName;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public MeetupEventGroupName getGroupNameObject() {
        return groupName;
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public int getAttendeesCount() {
        return attendeesCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventId);
        dest.writeString(eventName);
        dest.writeString(eventStatus);
        dest.writeString(eventDate);
        dest.writeString(eventTime);
        dest.writeValue(groupName);
        dest.writeString(eventUrl);
        dest.writeInt(attendeesCount);
    }
}
