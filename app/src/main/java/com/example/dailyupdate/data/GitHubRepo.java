package com.example.dailyupdate.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class GitHubRepo implements Parcelable {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("html_url")
    private String htmlUrl;
    @SerializedName("description")
    private String description;
    @SerializedName("stargazers_count")
    private int starCount;
    @SerializedName("language")
    private String language;

    public GitHubRepo(int id, String name, String htmlUrl, String description, int starCount,
                      String language) {
        this.id = id;
        this.name = name;
        this.htmlUrl = htmlUrl;
        this.description = description;
        this.starCount = starCount;
        this.language = language;
    }

    private GitHubRepo(Parcel in) {
        id = in.readInt();
        name = in.readString();
        htmlUrl = in.readString();
        description = in.readString();
        starCount = in.readInt();
        language = in.readString();
    }

    public static final Creator<GitHubRepo> CREATOR = new Creator<GitHubRepo>() {
        @Override
        public GitHubRepo createFromParcel(Parcel in) {
            return new GitHubRepo(in);
        }

        @Override
        public GitHubRepo[] newArray(int size) {
            return new GitHubRepo[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public String getDescription() {
        return description;
    }

    public int getStarCount() {
        return starCount;
    }

    public String getLanguage() {
        return language;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(htmlUrl);
        dest.writeString(description);
        dest.writeInt(starCount);
        dest.writeString(language);
    }
}
