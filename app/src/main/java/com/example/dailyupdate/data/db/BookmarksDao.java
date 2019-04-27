package com.example.dailyupdate.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.dailyupdate.data.model.MeetupEventDetails;

import java.util.List;

@Dao
public interface BookmarksDao {

    @Query("SELECT * FROM bookmarked_event ORDER BY event_id DESC")
    LiveData<List<MeetupEventDetails>> loadAllBookmarkedEvents();

    @Insert
    void insertBookmarkedEvent(MeetupEventDetails bookmarkedEvent);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateBookmarkedEvent(MeetupEventDetails bookmarkedEvent);

    @Delete
    void deleteBookmarkedEvent(MeetupEventDetails bookmarkedEvent);

}
