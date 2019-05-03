package com.example.dailyupdate.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.dailyupdate.R;
import com.example.dailyupdate.data.models.MeetupEventDetails;
import com.example.dailyupdate.repositories.BookmarksDatabaseRepository;
import com.example.dailyupdate.utilities.Constants;
import com.example.dailyupdate.utilities.DateUtilities;

import java.util.List;

public class WidgetAdapterService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetListAdapter(getApplicationContext(), intent);
    }

    class WidgetListAdapter implements RemoteViewsService.RemoteViewsFactory {

        private List<MeetupEventDetails> eventsList;
        private Context context;


        public WidgetListAdapter(Context applicationContext, Intent intent) {
            this.context = applicationContext;
        }

        @Override
        public void onCreate() {
        }

        /**
         * Called after onCreate and when notifyAppWidgetViewDataChanged is called.
         * Refresh data (also called the first time to fetch data), run long running task so we
         * don't have to open background thread even if it's a blocking operation.
         * As long as this is loading, the user will see the old data or loading view.
         * https://developer.android.com/reference/android/widget/RemoteViewsService
         * .RemoteViewsFactory#onDataSetChanged()
         * https://stackoverflow.com/questions/51973927/android-how-to-access-room-database-from-widget
         **/
        @Override
        public void onDataSetChanged() {
            BookmarksDatabaseRepository databaseRepository =
                    new BookmarksDatabaseRepository(getApplication());
            eventsList = databaseRepository.getAllEventsForWidget();
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public int getCount() {
            if (null == eventsList) return 0;
            return eventsList.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.widget_list_item);
            MeetupEventDetails bookmarkedEvent = eventsList.get(position);
            String dateWithDay = DateUtilities.getDateWithDay(bookmarkedEvent.getEventDate());
            String formattedTime = DateUtilities.getFormattedTime(bookmarkedEvent.getEventTime());

            views.setTextViewText(R.id.textview_event_title_widget, bookmarkedEvent.getEventName());
            views.setTextViewText(R.id.textview_group_title_widget,
                    bookmarkedEvent.getMeetupEventGroupName().getEventGroupName());
            views.setTextViewText(R.id.textview_event_date_widget, dateWithDay);
            views.setTextViewText(R.id.textview_event_time_widget, formattedTime);

            // Customize what kind of data we want to receive
            Intent fillIntent = new Intent();
            String groupUrl = bookmarkedEvent.getMeetupEventGroupName().getEventGroupUrl();
            String eventId = bookmarkedEvent.getEventId();
            fillIntent.putExtra(Constants.EXTRA_GROUP_URL, groupUrl);
            fillIntent.putExtra(Constants.EXTRA_EVENT_ID, eventId);
            views.setOnClickFillInIntent(R.id.bookmarked_event_widget_item, fillIntent);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

}
