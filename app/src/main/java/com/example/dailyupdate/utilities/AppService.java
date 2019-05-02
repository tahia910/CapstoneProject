package com.example.dailyupdate.utilities;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.example.dailyupdate.ui.activities.BookmarksActivity;
import com.example.dailyupdate.ui.activities.MainViewActivity;
import com.example.dailyupdate.utilities.notifications.NotificationUtilities;

public class AppService extends IntentService {

    public AppService() {
        super("AppService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            Context context = getApplicationContext();
            if (Constants.ACTION_OPEN_SEARCH.equals(action)) {
                // Open MainViewActivity to display the Meetup search
                Intent startActivityIntent = new Intent(context, MainViewActivity.class);
                startActivityIntent.putExtra(Constants.MAIN_KEY, Constants.MEETUP_MAIN_KEY);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 5,
                        startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                try {
                    pendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
                NotificationUtilities.clearAllNotifications(context);

            } else if (Constants.ACTION_DISMISS_NOTIFICATION.equals(action)) {
                // Dismiss the notification
                NotificationUtilities.clearAllNotifications(context);
            } else if (Constants.ACTION_GET_UPDATE_NOTIFICATION.equals(action)) {
                // Display the notification
                NotificationUtilities.createNotification(context);
            } else if (Constants.ACTION_OPEN_DETAILS.equals(action)){
                // Open BookmarksActivity to display the details of an event selected from the
                // widget
                String groupUrl = intent.getStringExtra(Constants.EXTRA_GROUP_URL);
                String eventId = intent.getStringExtra(Constants.EXTRA_EVENT_ID);
                Intent startBookmarksIntent = new Intent(context, BookmarksActivity.class);
                startBookmarksIntent.putExtra(Constants.EXTRA_GROUP_URL, groupUrl);
                startBookmarksIntent.putExtra(Constants.EXTRA_EVENT_ID, eventId);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 7,
                        startBookmarksIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                try {
                    pendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
