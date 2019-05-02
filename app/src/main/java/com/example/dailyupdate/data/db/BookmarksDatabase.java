package com.example.dailyupdate.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.dailyupdate.data.models.MeetupEventDetails;

@Database(entities = {MeetupEventDetails.class}, version = 1, exportSchema = false)
public abstract class BookmarksDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "bookmarked_event_database";
    private static BookmarksDatabase instance;

    public static synchronized BookmarksDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    BookmarksDatabase.class, BookmarksDatabase.DATABASE_NAME).build();
        }
        return instance;
    }

    public abstract BookmarksDao bookmarksDao();

}
