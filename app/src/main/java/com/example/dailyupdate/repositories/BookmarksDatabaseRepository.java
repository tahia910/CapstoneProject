package com.example.dailyupdate.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.dailyupdate.data.db.BookmarksDao;
import com.example.dailyupdate.data.db.BookmarksDatabase;
import com.example.dailyupdate.networking.AppExecutors;
import com.example.dailyupdate.data.models.MeetupEventDetails;

import java.util.List;

public class BookmarksDatabaseRepository {

    private BookmarksDao bookmarksDao;
    private BookmarksDatabase database;
    private LiveData<List<MeetupEventDetails>> bookmarkedEvents;

    public BookmarksDatabaseRepository(Application application) {
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

    public void deleteAllEvents(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                bookmarksDao.deleteAllBookmarkedEvents();
            }
        });
    }
}
