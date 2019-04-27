package com.example.dailyupdate.data.db;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.dailyupdate.networking.AppExecutors;
import com.example.dailyupdate.data.model.MeetupEventDetails;

import java.util.List;

public class AppRepository {

    private BookmarksDao bookmarksDao;
    private BookmarksDatabase database;
    private LiveData<List<MeetupEventDetails>> bookmarkedEvents;

    public AppRepository(Application application) {
        database = BookmarksDatabase.getInstance(application);
        bookmarksDao = database.bookmarksDao();
        bookmarkedEvents = bookmarksDao.loadAllBookmarkedEvents();
    }

    public void insertEvent(MeetupEventDetails bookmarkedEvent) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                bookmarksDao.insertBookmarkedEvent(bookmarkedEvent);
            }
        });
    }

    public void updateEvent(MeetupEventDetails bookmarkedEvent) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                bookmarksDao.updateBookmarkedEvent(bookmarkedEvent);
            }
        });
    }

    public void deleteEvent(MeetupEventDetails bookmarkedEvent) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                bookmarksDao.deleteBookmarkedEvent(bookmarkedEvent);
            }
        });
    }

    public LiveData<List<MeetupEventDetails>> getAllEvents() {
        return bookmarkedEvents;
    }

}
