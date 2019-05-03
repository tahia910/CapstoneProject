package com.example.dailyupdate.data.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "latest_search")
public class LatestSearch {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "event_id")
    private String eventId;

    public LatestSearch(String eventId){
        this.eventId = eventId;
    }

    @Ignore
    public LatestSearch(){}

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
