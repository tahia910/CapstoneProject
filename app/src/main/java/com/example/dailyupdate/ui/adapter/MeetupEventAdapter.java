package com.example.dailyupdate.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyupdate.R;
import com.example.dailyupdate.data.MeetupEvent;
import com.example.dailyupdate.data.MeetupEventGroupName;

import java.util.List;

public class MeetupEventAdapter extends RecyclerView.Adapter<MeetupEventAdapter.MeetupEventAdapterViewHolder> {

    private Context context;
    private static ClickListener clickListener;
    private final List<MeetupEvent> meetupEventList;

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        MeetupEventAdapter.clickListener = clickListener;
    }
    public MeetupEventAdapter(Context context, List<MeetupEvent> meetupEventList) {
        this.context = context;
        this.meetupEventList = meetupEventList;
    }

    @Override
    public MeetupEventAdapterViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.meetup_main_event_item, parent, false);
        MeetupEventAdapterViewHolder viewHolder = new MeetupEventAdapterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MeetupEventAdapterViewHolder viewHolder, int i) {
        MeetupEvent meetupEvent = meetupEventList.get(i);
        String eventName = meetupEvent.getEventName();
        String eventStatus = meetupEvent.getEventStatus();
        String eventDate = meetupEvent.getEventDate();
        String eventTime = meetupEvent.getEventTime();
        String groupName = meetupEvent.getGroupNameObject().getEventGroupName();

        int attendeesCount = meetupEvent.getAttendeesCount();
        String attendeesCountString = String.valueOf(attendeesCount) + " members going";

        viewHolder.eventNameTextView.setText(eventName);
        viewHolder.eventStatusTextView.setText(eventStatus);
        viewHolder.eventDateTextView.setText(eventDate);
        viewHolder.eventTimeTextView.setText(eventTime);
        viewHolder.groupNameTextView.setText(groupName);
        viewHolder.attendeesCountTextView.setText(attendeesCountString);
    }

    @Override
    public int getItemCount() {
        if (null == meetupEventList) return 0;
        return meetupEventList.size();
    }

    public class MeetupEventAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView eventNameTextView;
        private final TextView eventStatusTextView;
        private final TextView eventDateTextView;
        private final TextView eventTimeTextView;
        private final TextView groupNameTextView;
        private final TextView attendeesCountTextView;


        private MeetupEventAdapterViewHolder(View view) {
            super(view);
            eventNameTextView = (TextView) view.findViewById(R.id.textview_event_title);
            eventStatusTextView = (TextView) view.findViewById(R.id.textview_event_status);
            eventDateTextView = (TextView) view.findViewById(R.id.textview_event_date);
            eventTimeTextView = (TextView) view.findViewById(R.id.textview_event_time);
            groupNameTextView = (TextView) view.findViewById(R.id.textview_group_title);
            attendeesCountTextView = (TextView) view.findViewById(R.id.textview_event_attendees);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }
}

