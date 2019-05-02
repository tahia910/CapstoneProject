package com.example.dailyupdate.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.dailyupdate.R;
import com.example.dailyupdate.data.models.MeetupEventDetails;
import com.example.dailyupdate.data.models.MeetupEventLocation;
import com.example.dailyupdate.utilities.Constants;
import com.example.dailyupdate.utilities.DateUtilities;
import com.example.dailyupdate.viewmodels.BookmarksDatabaseViewModel;
import com.example.dailyupdate.viewmodels.MeetupViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeetupDetailsFragment extends DialogFragment {

    @BindView(R.id.spinner_meetup_detail) ProgressBar spinner;
    @BindView(R.id.emptyview_meetup_detail) TextView emptyView;

    @BindView(R.id.textview_event_title_meetup_detail)
    TextView eventTitleTextView;
    @BindView(R.id.textview_group_title_meetup_detail)
    TextView groupTextView;
    @BindView(R.id.textview_status_meetup_detail)
    TextView statusTextView;
    @BindView(R.id.textview_attendees_meetup_detail)
    TextView attendeesTextView;
    @BindView(R.id.textview_waitlist_meetup_detail)
    TextView waitlistCountTextView;
    @BindView(R.id.textview_place_left_meetup_detail)
    TextView placeLeftTextView;
    @BindView(R.id.textview_date_meetup_detail)
    TextView dateTextView;
    @BindView(R.id.textview_time_meetup_detail)
    TextView timeTextView;
    @BindView(R.id.textview_address_meetup_detail)
    TextView addressTextView;
    @BindView(R.id.textview_description_label_meetup_detail)
    TextView descriptionTitleTextView;
    @BindView(R.id.textview_description_meetup_detail)
    TextView descriptionTextView;

    @BindView(R.id.imageview_group_icon_meetup_detail) ImageView groupIcon;
    @BindView(R.id.imageview_status_icon_meetup_detail) ImageView statusIcon;
    @BindView(R.id.imageview_time_icon_meetup_detail) ImageView timeIcon;
    @BindView(R.id.imageview_place_icon_meetup_detail) ImageView placeIcon;

    //    @BindView(R.id.back_icon_meetup_detail)
    private ImageView backIcon;
    //    @BindView(R.id.bookmark_icon_meetup_detail)
    private ImageView bookmarkIcon;

    private Dialog dialog;
    private BookmarksDatabaseViewModel databaseViewModel;
    private MeetupViewModel meetupViewModel;

    private String groupId;
    private String eventId;
    private MeetupEventDetails currentEvent;
    private Boolean alreadyBookmarked;

    public static MeetupDetailsFragment newInstance(String groupUrl, String eventId) {
        MeetupDetailsFragment meetupDetailsFragment = new MeetupDetailsFragment();
        Bundle args = new Bundle();
        args.putString(Constants.KEY_GROUP_URL, groupUrl);
        args.putString(Constants.KEY_EVENT_ID, eventId);
        meetupDetailsFragment.setArguments(args);
        return meetupDetailsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.meetup_detail, container, false);
        ButterKnife.bind(this, rootView);
        spinner.setVisibility(View.VISIBLE);
        if (savedInstanceState != null) {
            groupId = savedInstanceState.getString(Constants.KEY_GROUP_URL);
            eventId = savedInstanceState.getString(Constants.KEY_EVENT_ID);
        } else {
            groupId = getArguments().getString(Constants.KEY_GROUP_URL);
            eventId = getArguments().getString(Constants.KEY_EVENT_ID);
        }
        databaseViewModel =
                ViewModelProviders.of(MeetupDetailsFragment.this).get(BookmarksDatabaseViewModel.class);
        meetupViewModel =
                ViewModelProviders.of(MeetupDetailsFragment.this).get(MeetupViewModel.class);
        subscribeMeetupEventDetailsObserver();
        meetupViewModel.searchMeetupEventDetails(groupId, eventId);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        setCustomActionBar();
    }

    private void setCustomActionBar() {
        dialog = MeetupDetailsFragment.this.getDialog();
        backIcon = dialog.findViewById(R.id.back_icon_meetup_detail);
        backIcon.setOnClickListener(v -> dialog.dismiss());
        checkAlreadyBookmarked();
    }

    // TODO: change "alreadyBookmarked" logic
    private void checkAlreadyBookmarked() {
        bookmarkIcon = dialog.findViewById(R.id.bookmark_icon_meetup_detail);
        databaseViewModel.getAllBookmarkedEvents().observe(MeetupDetailsFragment.this,
                new Observer<List<MeetupEventDetails>>() {
            @Override
            public void onChanged(List<MeetupEventDetails> meetupEventDetails) {
                ArrayList<String> array = new ArrayList<>();
                for (int i = 0; i < meetupEventDetails.size(); i++) {
                    final MeetupEventDetails bookmarkItem = meetupEventDetails.get(i);
                    String currentEventId = bookmarkItem.getEventId();
                    array.add(currentEventId);
                }
                if (!array.contains(eventId)) {
                    alreadyBookmarked = false;
                    bookmarkIcon.setImageResource(R.drawable.ic_bookmark_white);
                } else {
                    alreadyBookmarked = true;
                    bookmarkIcon.setImageResource(R.drawable.ic_bookmarked_white);
                }
                setBookmarkIconClickListener();
            }
        });
    }

    private void setBookmarkIconClickListener() {
        bookmarkIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alreadyBookmarked) {
                    alreadyBookmarked = false;
                    databaseViewModel.deleteBookmarkedEvent(currentEvent);
                    bookmarkIcon.setImageResource(R.drawable.ic_bookmark_white);
                } else {
                    alreadyBookmarked = true;
                    databaseViewModel.insertBookmarkedEvent(currentEvent);
                    bookmarkIcon.setImageResource(R.drawable.ic_bookmarked_white);
                }
            }
        });
    }


    private void subscribeMeetupEventDetailsObserver() {
        meetupViewModel.getMeetupEventDetails().observe(this, new Observer<MeetupEventDetails>() {
            @Override
            public void onChanged(MeetupEventDetails currentMeetupEvent) {
                if (currentMeetupEvent != null) {
                    spinner.setVisibility(View.INVISIBLE);
                    currentEvent = currentMeetupEvent;
                    setEventInformation(currentEvent);
                }
            }
        });
    }

    private void setEventInformation(MeetupEventDetails meetupEventDetails) {
        String eventName = meetupEventDetails.getEventName();
        String groupName = meetupEventDetails.getMeetupEventGroupName().getEventGroupName();
        String status = "Status: " + meetupEventDetails.getEventStatus();

        int attendeesCount = meetupEventDetails.getEventAttendees();
        String attendeesCountString =
                attendeesCount + getString(R.string.meetupevent_attendees_label);

        String waitlistCountString = meetupEventDetails.getWaitlistCount() + " members on waitlist";

        int maximumAttendees = meetupEventDetails.getMaximumAttendees();
        String placeLeftString = (maximumAttendees - attendeesCount) + " places left";

        MeetupEventLocation locationObject = meetupEventDetails.getLocationObject();
        String placeName = locationObject.getPlaceName();
        String address = locationObject.getAddress();
        String city = locationObject.getCity();
        String country = locationObject.getCountry();
        String addressString = placeName + ", " + address + ", " + city + ", " + country;

        String dateWithDay = DateUtilities.getDateWithDay(meetupEventDetails.getEventDate());
        String formattedTime = DateUtilities.getFormattedTime(meetupEventDetails.getEventTime());

        eventTitleTextView.setText(eventName);

        groupIcon.setVisibility(View.VISIBLE);
        groupTextView.setText(groupName);

        statusIcon.setVisibility(View.VISIBLE);
        statusTextView.setText(status);
        placeLeftTextView.setText(placeLeftString);
        attendeesTextView.setText(attendeesCountString);
        waitlistCountTextView.setText(waitlistCountString);

        timeIcon.setVisibility(View.VISIBLE);
        dateTextView.setText(dateWithDay);
        timeTextView.setText(formattedTime);

        placeIcon.setVisibility(View.VISIBLE);
        addressTextView.setText(addressString);

        descriptionTitleTextView.setVisibility(View.VISIBLE);
        descriptionTextView.setText(meetupEventDetails.getEventDescription());
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putString(Constants.KEY_GROUP_URL, groupId);
        currentState.putString(Constants.KEY_EVENT_ID, eventId);
        super.onSaveInstanceState(currentState);
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
//        getActivity().requestWindowFeature(Window.FEATURE_ACTION_BAR);
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }

}

//    private void openLocationInMap() {
//        String addressString = "1600 Ampitheatre Parkway, CA";
//        Uri geoLocation = Uri.parse("geo:0,0?q=" + addressString);
//
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(geoLocation);
//
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        } else {
//            Log.d(TAG, "Couldn't call " + geoLocation.toString()
//                    + ", no receiving apps installed!");
//        }

