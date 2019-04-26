package com.example.dailyupdate.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MeetupEventGroupName implements Parcelable {
    @SerializedName("name")
    private String eventGroupName;
    @SerializedName("urlname")
    private String eventGroupUrl;

    public MeetupEventGroupName(String eventGroupName, String eventGroupUrl) {
        this.eventGroupName = eventGroupName;
        this.eventGroupUrl = eventGroupUrl;
    }

    private MeetupEventGroupName(Parcel in) {
        eventGroupName = in.readString();
        eventGroupUrl = in.readString();
    }

    public static final Creator<MeetupEventGroupName> CREATOR =
            new Creator<MeetupEventGroupName>() {
        @Override
        public MeetupEventGroupName createFromParcel(Parcel in) {
            return new MeetupEventGroupName(in);
        }

        @Override
        public MeetupEventGroupName[] newArray(int size) {
            return new MeetupEventGroupName[size];
        }
    };

    public String getEventGroupName() {
        return eventGroupName;
    }

    public String getEventGroupUrl(){return eventGroupUrl;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventGroupName);
        dest.writeString(eventGroupUrl);
    }
}
