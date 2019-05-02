package com.example.dailyupdate.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MeetupGroupImage implements Parcelable {

    @SerializedName("id")
    private int imageId;
    @SerializedName("photo_link")
    private String groupPhotoUrl;

    public MeetupGroupImage(int imageId, String groupPhotoUrl) {
        this.imageId = imageId;
        this.groupPhotoUrl = groupPhotoUrl;
    }

    private MeetupGroupImage(Parcel in) {
        imageId = in.readInt();
        groupPhotoUrl = in.readString();
    }

    public static final Creator<MeetupGroupImage> CREATOR = new Creator<MeetupGroupImage>() {
        @Override
        public MeetupGroupImage createFromParcel(Parcel in) {
            return new MeetupGroupImage(in);
        }

        @Override
        public MeetupGroupImage[] newArray(int size) {
            return new MeetupGroupImage[size];
        }
    };

    public int getImageId() {
        return imageId;
    }

    public String getGroupPhotoUrl() {
        return groupPhotoUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(imageId);
        dest.writeString(groupPhotoUrl);
    }
}
