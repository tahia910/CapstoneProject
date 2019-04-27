package com.example.dailyupdate.data.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MeetupGroup implements Parcelable {

    @SerializedName("id")
    private int groupId;
    @SerializedName("name")
    private String groupName;
    @SerializedName("link")
    private String groupUrl;
    @SerializedName("description")
    private String groupDescription;
    @SerializedName("members")
    private int groupMembers;
    @SerializedName("group_photo")
    private MeetupGroupImage groupPhoto;

    public MeetupGroup(int groupId, String groupName, String groupUrl, String groupDescription,
                       int groupMembers, MeetupGroupImage groupPhoto) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupUrl = groupUrl;
        this.groupDescription = groupDescription;
        this.groupMembers = groupMembers;
        this.groupPhoto = groupPhoto;
    }

    @SuppressLint("NewApi")
    private MeetupGroup(Parcel in) {
        groupId = in.readInt();
        groupName = in.readString();
        groupUrl = in.readString();
        groupDescription = in.readString();
        groupMembers = in.readInt();
        groupPhoto = in.readTypedObject(MeetupGroupImage.CREATOR);
    }

    public static final Creator<MeetupGroup> CREATOR = new Creator<MeetupGroup>() {
        @Override
        public MeetupGroup createFromParcel(Parcel in) {
            return new MeetupGroup(in);
        }

        @Override
        public MeetupGroup[] newArray(int size) {
            return new MeetupGroup[size];
        }
    };

    public int getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupUrl() {
        return groupUrl;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public int getGroupMembers() {
        return groupMembers;
    }

    public MeetupGroupImage getGroupPhoto() {
        return groupPhoto;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(groupId);
        dest.writeString(groupName);
        dest.writeString(groupUrl);
        dest.writeString(groupDescription);
        dest.writeInt(groupMembers);
        dest.writeValue(groupPhoto);
    }
}
