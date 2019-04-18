package com.example.dailyupdate.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MeetupGroup {

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
}
