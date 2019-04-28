package com.example.dailyupdate;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.dailyupdate.data.db.AppRepository;
import com.example.dailyupdate.data.model.MeetupEventDetails;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private AppRepository repository;
    private LiveData<List<MeetupEventDetails>> bookmarkedEvents;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
        bookmarkedEvents = repository.getAllEvents();
    }

    public void insertBookmarkedEvent(MeetupEventDetails bookmarkedEvent){
        repository.insertEvent(bookmarkedEvent);
    }

    public void updateBookmarkedEvent(MeetupEventDetails bookmarkedEvent){
        repository.updateEvent(bookmarkedEvent);
    }

    public void deleteBookmarkedEvent(MeetupEventDetails bookmarkedEvent){
        repository.deleteEvent(bookmarkedEvent);
    }

    public LiveData<List<MeetupEventDetails>> getAllBookmarkedEvents(){
        return bookmarkedEvents;
    }

    public void deleteAllBookmarkedEvent(){
        repository.deleteAllEvents();
    }
}
