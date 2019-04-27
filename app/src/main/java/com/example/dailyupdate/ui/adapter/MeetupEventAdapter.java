package com.example.dailyupdate.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyupdate.R;
import com.example.dailyupdate.data.model.MeetupEvent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeetupEventAdapter extends RecyclerView.Adapter<MeetupEventAdapter.MeetupEventAdapterViewHolder> {

    @BindView(R.id.textview_event_title) TextView eventNameTextView;
    @BindView(R.id.textview_event_status) TextView eventStatusTextView;
    @BindView(R.id.textview_event_date) TextView eventDateTextView;
    @BindView(R.id.textview_event_time) TextView eventTimeTextView;
    @BindView(R.id.textview_group_title) TextView groupNameTextView;
    @BindView(R.id.textview_event_attendees) TextView attendeesCountTextView;

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
        View view = LayoutInflater.from(context).inflate(R.layout.meetup_main_event_item, parent,
                false);
        ButterKnife.bind(this, view);
        return new MeetupEventAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MeetupEventAdapterViewHolder viewHolder, int i) {
        MeetupEvent meetupEvent = meetupEventList.get(i);
        // TODO: change date & time format
        String eventDate = meetupEvent.getEventDate();
        String eventTime = meetupEvent.getEventTime();
        String attendeesCountString = meetupEvent.getAttendeesCount() + context.getString(R.string.meetupevent_attendees_label);

        eventNameTextView.setText(meetupEvent.getEventName());
        eventStatusTextView.setText(meetupEvent.getEventStatus());
        eventDateTextView.setText(eventDate);
        eventTimeTextView.setText(eventTime);
        groupNameTextView.setText(meetupEvent.getGroupNameObject().getEventGroupName());
        attendeesCountTextView.setText(attendeesCountString);
    }

    @Override
    public int getItemCount() {
        if (null == meetupEventList) return 0;
        return meetupEventList.size();
    }

    public class MeetupEventAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private MeetupEventAdapterViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }
}

