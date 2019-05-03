package com.example.dailyupdate.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.dailyupdate.data.models.LatestSearch;

import java.util.List;

@Dao
public interface LatestSearchDao {

    @Query("SELECT event_id FROM latest_search ORDER BY event_id DESC")
    LiveData<List<String>> getAllLatestSearchLive();

    @Query("SELECT event_id FROM latest_search ORDER BY event_id DESC")
    List<String> getAllLatestSearch();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLatestSearch(List<LatestSearch> latestSearch);

    @Query("DELETE FROM latest_search")
    void deleteAllLatestSearch();

}
