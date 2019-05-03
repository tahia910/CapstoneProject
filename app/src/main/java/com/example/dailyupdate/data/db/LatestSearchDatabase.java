package com.example.dailyupdate.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.dailyupdate.data.models.LatestSearch;

@Database(entities = {LatestSearch.class}, version = 1, exportSchema = false)
public abstract class LatestSearchDatabase extends RoomDatabase{

    private static final String DATABASE_NAME = "latest_search_database";
    private static LatestSearchDatabase instance;

    public static synchronized LatestSearchDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    LatestSearchDatabase.class, LatestSearchDatabase.DATABASE_NAME).build();
        }
        return instance;
    }

    public abstract LatestSearchDao latestSearchDao();
}
