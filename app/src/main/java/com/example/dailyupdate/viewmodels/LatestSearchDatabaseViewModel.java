package com.example.dailyupdate.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.dailyupdate.data.models.LatestSearch;
import com.example.dailyupdate.repositories.LatestSearchDatabaseRepository;

import java.util.List;

public class LatestSearchDatabaseViewModel extends AndroidViewModel {

    private LatestSearchDatabaseRepository repository;
    private LiveData<List<String>> latestSearchLive;

    public LatestSearchDatabaseViewModel(@NonNull Application application) {
        super(application);
        repository = new LatestSearchDatabaseRepository(application);
        latestSearchLive = repository.getAllLatestSearchLive();
    }

    public void insertLatestSearch(List<LatestSearch> latestSearch) {
        repository.insertSearchItem(latestSearch);
    }

    public void deleteAllEvents() {
        repository.deleteAllEvents();
    }

    public LiveData<List<String>> getAllLatestSearchLive() {
        return latestSearchLive;
    }

}

