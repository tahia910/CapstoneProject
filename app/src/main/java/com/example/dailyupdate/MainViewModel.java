package com.example.dailyupdate;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.dailyupdate.data.db.AppRepository;
import com.example.dailyupdate.data.model.MeetupEventDetails;
import com.example.dailyupdate.ui.widget.WidgetService;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private AppRepository repository;
    private LiveData<List<MeetupEventDetails>> bookmarkedEvents;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
        bookmarkedEvents = repository.getAllEvents();
    }

    public void insertBookmarkedEvent(MeetupEventDetails bookmarkedEvent) {
        repository.insertEvent(bookmarkedEvent);
        updateWidget();
    }

    public void updateBookmarkedEvent(MeetupEventDetails bookmarkedEvent) {
        repository.updateEvent(bookmarkedEvent);
    }

    public void deleteBookmarkedEvent(MeetupEventDetails bookmarkedEvent) {
        repository.deleteEvent(bookmarkedEvent);
        updateWidget();
    }

    public LiveData<List<MeetupEventDetails>> getAllBookmarkedEvents() {
        return bookmarkedEvents;
    }

    public void deleteAllBookmarkedEvent() {
        repository.deleteAllEvents();
        updateWidget();
    }

    private void updateWidget() {
        Intent intent = new Intent(getApplication(), WidgetService.class);
        intent.setAction(WidgetService.ACTION_UPDATE_WIDGET);
        getApplication().startService(intent);
    }
}
