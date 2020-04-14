package com.example.dailyupdate.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyupdate.R;
import com.example.dailyupdate.data.models.MeetupEvent;
import com.example.dailyupdate.utilities.DateUtilities;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeetupEventAdapter extends RecyclerView.Adapter<MeetupEventAdapter.MeetupEventAdapterViewHolder> {

    private Context context;
    private static ClickListener clickListener;
    private static BookmarkIconClickListener bookmarkIconListener;
    private final List<MeetupEvent> meetupEventList;
    private final List<String> bookmarkedEventsIds;

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        MeetupEventAdapter.clickListener = clickListener;
    }

    public interface BookmarkIconClickListener {
        void onBookmarkIconClick(MeetupEvent meetupEvent, int position);
    }

    public void setOnBookmarkIconClickListener(BookmarkIconClickListener bookmarkIconListener) {
        this.bookmarkIconListener = bookmarkIconListener;
    }

    public MeetupEventAdapter(Context context, List<MeetupEvent> meetupEventList,
                              List<String> bookmarkedEventsIds) {
        this.context = context;
        this.meetupEventList = meetupEventList;
        this.bookmarkedEventsIds = bookmarkedEventsIds;
    }

    @Override
    public MeetupEventAdapterViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.meetup_main_event_item, parent,
                false);
        return new MeetupEventAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MeetupEventAdapterViewHolder viewHolder, int i) {
        viewHolder.bindEvent(meetupEventList.get(i));
    }

    @Override
    public int getItemCount() {
        if (null == meetupEventList) return 0;
        return meetupEventList.size();
    }

    public class MeetupEventAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.textview_event_title)
        TextView eventNameTextView;
        @BindView(R.id.textview_event_date)
        TextView eventDateTextView;
        @BindView(R.id.textview_event_time)
        TextView eventTimeTextView;
        @BindView(R.id.textview_group_title)
        TextView groupNameTextView;
        @BindView(R.id.textview_event_attendees)
        TextView attendeesCountTextView;
        @BindView(R.id.bookmark_icon_meetup_main)
        ImageView bookmarkIcon;

        private MeetupEventAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

        void bindEvent(MeetupEvent meetupEvent) {
            String dateWithDay = DateUtilities.getDateWithDay(meetupEvent.getEventDate());
            String formattedTime = DateUtilities.getFormattedTime(meetupEvent.getEventTime());
            String attendeesCountString =
                    meetupEvent.getAttendeesCount() + context.getString(R.string.meetupevent_attendees_label);

            eventNameTextView.setText(meetupEvent.getEventName());
            eventDateTextView.setText(dateWithDay);
            eventTimeTextView.setText(formattedTime);
            groupNameTextView.setText(meetupEvent.getGroupNameObject().getEventGroupName());
            attendeesCountTextView.setText(attendeesCountString);
            if (bookmarkedEventsIds != null) {
                if (bookmarkedEventsIds.contains(meetupEvent.getEventId())) {
                    bookmarkIcon.setImageResource(R.drawable.ic_bookmarked);
                } else {
                    bookmarkIcon.setImageResource(R.drawable.ic_bookmark);
                }
            }

            bookmarkIcon.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (bookmarkIconListener != null && position != RecyclerView.NO_POSITION) {
                    bookmarkIconListener.onBookmarkIconClick(meetupEvent, position);
                }
            });
        }
    }
}