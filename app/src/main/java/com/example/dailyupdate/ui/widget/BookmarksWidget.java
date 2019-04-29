package com.example.dailyupdate.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.dailyupdate.R;
import com.example.dailyupdate.ui.activity.BookmarksActivity;

/**
 * Implementation of App Widget functionality.
 */
public class BookmarksWidget extends AppWidgetProvider {

    public static final String ACTION_ITEM_CLICK = "com.example.dailyupdate.ui.widget.action_toast";
    public static final String EXTRA_GROUP_URL = "com.example.dailyupdate.ui.widget" +
            ".extra_group_url";
    public static final String EXTRA_EVENT_ID = "com.example.dailyupdate.ui.widget.extra_event_id";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            // Set ClickListener on each item in the adapter
            Intent broadcastIntent = new Intent(context, BookmarksWidget.class);
            broadcastIntent.setAction(ACTION_ITEM_CLICK);
            PendingIntent broadcastPendingIntent = PendingIntent.getBroadcast(context, 0,
                    broadcastIntent, 0);
            views.setPendingIntentTemplate(R.id.appwidget_listview, broadcastPendingIntent);

            // Attach adapter, set empty view for when the listview doesn't have items
            Intent adapterIntent = new Intent(context, WidgetAdapterService.class);
            views.setRemoteAdapter(R.id.appwidget_listview, adapterIntent);
            views.setEmptyView(R.id.appwidget_listview, R.id.appwidget_empty_view);

            appWidgetManager.updateAppWidget(appWidgetId, views);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.appwidget_listview);
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_ITEM_CLICK.equals(intent.getAction())) {
            String groupUrl = intent.getStringExtra(EXTRA_GROUP_URL);
            String eventId = intent.getStringExtra(EXTRA_EVENT_ID);

            Intent bookmarksActivityIntent = new Intent(context, BookmarksActivity.class);
            bookmarksActivityIntent.putExtra(EXTRA_GROUP_URL, groupUrl);
            bookmarksActivityIntent.putExtra(EXTRA_EVENT_ID, eventId);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, bookmarksActivityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        } super.onReceive(context, intent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }
}

