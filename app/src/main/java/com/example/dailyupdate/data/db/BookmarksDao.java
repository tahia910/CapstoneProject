package com.example.dailyupdate.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.dailyupdate.data.models.MeetupEventDetails;

import java.util.List;

@Dao
public interface BookmarksDao {

    @Query("SELECT * FROM bookmarked_event ORDER BY event_id DESC")
    LiveData<List<MeetupEventDetails>> loadAllBookmarkedEvents();

    @Query("SELECT * FROM bookmarked_event ORDER BY event_id DESC")
    List<MeetupEventDetails> loadAllBookmarkedEventsForWidget();

    @Query("SELECT event_id FROM bookmarked_event ORDER BY event_id DESC")
    LiveData<List<String>> getAllBookmarkedEventsIds();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBookmarkedEvent(MeetupEventDetails bookmarkedEvent);

    @Delete
    void deleteBookmarkedEvent(MeetupEventDetails bookmarkedEvent);

    @Query("DELETE FROM bookmarked_event")
    void deleteAllBookmarkedEvents();
}
