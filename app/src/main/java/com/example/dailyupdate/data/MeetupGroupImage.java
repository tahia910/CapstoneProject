package com.example.dailyupdate.data;

import com.google.gson.annotations.SerializedName;

public class MeetupGroupImage {

    @SerializedName("id")
    private int imageId;
    @SerializedName("photo_link")
    private String groupPhotoUrl;

    public MeetupGroupImage(int imageId, String groupPhotoUrl) {
        this.imageId = imageId;
        this.groupPhotoUrl = groupPhotoUrl;
    }

    public int getImageId() {
        return imageId;
    }

    public String getGroupPhotoUrl() {
        return groupPhotoUrl;
    }

}
