package com.example.dailyupdate.viewmodels;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.dailyupdate.repositories.BookmarksDatabaseRepository;
import com.example.dailyupdate.data.models.MeetupEventDetails;
import com.example.dailyupdate.utilities.Constants;
import com.example.dailyupdate.widget.WidgetService;

import java.util.List;

public class BookmarksDatabaseViewModel extends AndroidViewModel {

    private BookmarksDatabaseRepository repository;
    private LiveData<List<MeetupEventDetails>> bookmarkedEvents;
    private LiveData<List<String>> bookmarkedEventsIds;

    public BookmarksDatabaseViewModel(@NonNull Application application) {
        super(application);
        repository = new BookmarksDatabaseRepository(application);
        bookmarkedEvents = repository.getAllEvents();
        bookmarkedEventsIds = repository.getAllEventsIds();
    }

    public void insertBookmarkedEvent(MeetupEventDetails bookmarkedEvent) {
        repository.insertEvent(bookmarkedEvent);
        updateWidget();
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
        intent.setAction(Constants.ACTION_UPDATE_WIDGET);
        getApplication().startService(intent);
    }

    public LiveData<List<String>> getAllBookmarkedEventsIds(){
        return bookmarkedEventsIds;
    }

}
