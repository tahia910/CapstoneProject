package com.example.dailyupdate.utilities;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.example.dailyupdate.ui.activity.MainViewActivity;
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
            }
        }
    }


}
