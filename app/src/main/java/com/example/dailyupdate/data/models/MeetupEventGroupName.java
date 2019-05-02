package com.example.dailyupdate.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(foreignKeys = @ForeignKey(
        entity = MeetupEventDetails.class,
        parentColumns = "event_id",
        childColumns = "group_name"))
public class MeetupEventGroupName implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "group_name")
    @SerializedName("name")
    private String eventGroupName;

    @ColumnInfo(name = "group_url")
    @SerializedName("urlname")
    private String eventGroupUrl;

    public MeetupEventGroupName(String eventGroupName, String eventGroupUrl) {
        this.eventGroupName = eventGroupName;
        this.eventGroupUrl = eventGroupUrl;
    }

    @Ignore
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

    public void setEventGroupName(String eventGroupName) {
        this.eventGroupName = eventGroupName;
    }

    public String getEventGroupUrl() {
        return eventGroupUrl;
    }

    public void setEventGroupUrl(String eventGroupUrl) {
        this.eventGroupUrl = eventGroupUrl;
    }

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
