package com.example.dailyupdate.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.dailyupdate.data.db.LatestSearchDao;
import com.example.dailyupdate.data.db.LatestSearchDatabase;
import com.example.dailyupdate.data.models.LatestSearch;
import com.example.dailyupdate.networking.AppExecutors;

import java.util.List;

public class LatestSearchDatabaseRepository {

    private LatestSearchDao dao;
    private LatestSearchDatabase database;
    private LiveData<List<String>> latestSearchResultLive;

    public LatestSearchDatabaseRepository(Application application) {
        database = LatestSearchDatabase.getInstance(application);
        dao = database.latestSearchDao();
        latestSearchResultLive = dao.getAllLatestSearchLive();
    }

    public void insertSearchItem(List<LatestSearch> latestSearch) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                dao.insertLatestSearch(latestSearch);
            }
        });
    }

    public void deleteAllEvents() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                dao.deleteAllLatestSearch();
            }
        });
    }

    public LiveData<List<String>> getAllLatestSearchLive() {
        return latestSearchResultLive;
    }

    /**
     * This method is used for the FirebaseJobService and will be wrapped in an AsyncTask there
     **/
    public List<String> getAllLatestSearch() {
        return dao.getAllLatestSearch();
    }
}
