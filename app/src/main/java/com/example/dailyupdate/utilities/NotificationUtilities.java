package com.example.dailyupdate.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.dailyupdate.R;
import com.example.dailyupdate.ui.activity.MainActivity;
import com.example.dailyupdate.ui.activity.MainViewActivity;

/**
 * Utility class for creating update notifications
 */
public class NotificationUtilities {

    private static final int UPDATE_REMINDER_NOTIFICATION_ID = 16;

    private static final int UPDATE_REMINDER_PENDING_INTENT_ID = 34;

    private static final String UPDATE_REMINDER_NOTIFICATION_CHANNEL_ID =
            "update_notification_channel";

    private static final int ACTION_OPEN_SEARCH_PENDING_INTENT_ID = 23;
    private static final int ACTION_IGNORE_PENDING_INTENT_ID = 64;

    public static void createNotification(Context context) {
        NotificationManager notifManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for Android O devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notifChannel =
                    new NotificationChannel(UPDATE_REMINDER_NOTIFICATION_CHANNEL_ID, context.getString(R.string.notif_channel_title),
                            NotificationManager.IMPORTANCE_HIGH);
            notifManager.createNotificationChannel(notifChannel);
        }

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context,
                UPDATE_REMINDER_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setContentTitle(context.getString(R.string.notif_content_title))
                .setContentText(context.getString(R.string.notif_content_text))
                .setSmallIcon(R.drawable.ic_search)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notif_big_text))).setContentIntent(contentIntent(context))
                .setAutoCancel(true)
                .addAction(openSearchAction(context))
                .addAction(ignoreUpdateAction(context));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notifBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        notifManager.notify(UPDATE_REMINDER_NOTIFICATION_ID, notifBuilder.build());
    }


    public static void clearAllNotifications(Context context){
        NotificationManager notifManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();
    }

    private static NotificationCompat.Action ignoreUpdateAction(Context context){
        Intent ignoreUpdateIntent = new Intent(context, AppService.class);
        ignoreUpdateIntent.setAction(AppService.ACTION_DISMISS_NOTIFICATION);
        PendingIntent ignoreUpdatePendingIntent = PendingIntent.getService(context,
                ACTION_IGNORE_PENDING_INTENT_ID , ignoreUpdateIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action ignoreUpdateAction =
                new NotificationCompat.Action(R.drawable.ic_cancel, context.getString(R.string.notif_action_ignore),
                        ignoreUpdatePendingIntent);
        return ignoreUpdateAction;
    }

    private static NotificationCompat.Action openSearchAction(Context context){
        Intent ignoreUpdateIntent = new Intent(context, AppService.class);
        ignoreUpdateIntent.setAction(AppService.ACTION_OPEN_SEARCH);
        PendingIntent ignoreUpdatePendingIntent = PendingIntent.getService(context,
                ACTION_OPEN_SEARCH_PENDING_INTENT_ID, ignoreUpdateIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action ignoreUpdateAction =
                new NotificationCompat.Action(R.drawable.ic_open, context.getString(R.string.notif_action_open),
                        ignoreUpdatePendingIntent);
        return ignoreUpdateAction;
    }

    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, MainViewActivity.class);
        startActivityIntent.putExtra(MainActivity.MAIN_KEY, MainActivity.MEETUP_MAIN_KEY);
        return PendingIntent.getActivity(context, UPDATE_REMINDER_PENDING_INTENT_ID,
                startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
