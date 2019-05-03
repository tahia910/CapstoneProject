package com.example.dailyupdate.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.dailyupdate.data.db.BookmarksDao;
import com.example.dailyupdate.data.db.BookmarksDatabase;
import com.example.dailyupdate.data.models.MeetupEventDetails;
import com.example.dailyupdate.networking.AppExecutors;

import java.util.List;

public class BookmarksDatabaseRepository {

    private BookmarksDao bookmarksDao;
    private BookmarksDatabase database;
    private LiveData<List<MeetupEventDetails>> bookmarkedEvents;
    private LiveData<List<String>> bookmarkedEventsIds;

    public BookmarksDatabaseRepository(Application application) {
        database = BookmarksDatabase.getInstance(application);
        bookmarksDao = database.bookmarksDao();
        bookmarkedEvents = bookmarksDao.loadAllBookmarkedEvents();
        bookmarkedEventsIds = bookmarksDao.getAllBookmarkedEventsIds();
    }

    public void insertEvent(MeetupEventDetails bookmarkedEvent) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                bookmarksDao.insertBookmarkedEvent(bookmarkedEvent);
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

    public void deleteAllEvents() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                bookmarksDao.deleteAllBookmarkedEvents();
            }
        });
    }

    public LiveData<List<String>> getAllEventsIds() {
        return bookmarkedEventsIds;
    }

    /**
     * This method is used for the widget and will be run asynchronously
     **/
    public List<MeetupEventDetails> getAllEventsForWidget() {
        return bookmarksDao.loadAllBookmarkedEventsForWidget();
    }

}
