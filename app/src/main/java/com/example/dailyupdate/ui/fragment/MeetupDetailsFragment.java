package com.example.dailyupdate.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.dailyupdate.BuildConfig;
import com.example.dailyupdate.R;
import com.example.dailyupdate.data.MeetupEventDetails;
import com.example.dailyupdate.data.MeetupEventLocation;
import com.example.dailyupdate.networking.MeetupService;
import com.example.dailyupdate.networking.RetrofitInstance;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.dailyupdate.ui.MainViewActivity.KEY_EVENT_ID;
import static com.example.dailyupdate.ui.MainViewActivity.KEY_GROUP_URL;

public class MeetupDetailsFragment extends DialogFragment {

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
    @BindView(R.id.textview_description_meetup_detail)
    TextView eventDescriptionTextView;

    //    @BindView(R.id.back_icon_meetup_detail)
    ImageView backIcon;
    //    @BindView(R.id.bookmark_icon_meetup_detail)
    ImageView bookmarkIcon;

    Dialog dialog;


    private String API_KEY = BuildConfig.MEETUP_API_KEY;
    private String groupId;
    private String eventId;

    public static MeetupDetailsFragment newInstance(String groupUrl, String eventId) {
        MeetupDetailsFragment meetupDetailsFragment = new MeetupDetailsFragment();
        Bundle args = new Bundle();
        args.putString(KEY_GROUP_URL, groupUrl);
        args.putString(KEY_EVENT_ID, eventId);
        meetupDetailsFragment.setArguments(args);
        return meetupDetailsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.meetup_detail, container, false);
        ButterKnife.bind(this, rootView);
        if (savedInstanceState != null) {
            groupId = savedInstanceState.getString(KEY_GROUP_URL);
            eventId = savedInstanceState.getString(KEY_EVENT_ID);
        } else {
            groupId = getArguments().getString(KEY_GROUP_URL);
            eventId = getArguments().getString(KEY_EVENT_ID);
        }
        retrieveEventDetails();
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
        bookmarkIcon = dialog.findViewById(R.id.bookmark_icon_meetup_detail);
        bookmarkIcon.setOnClickListener(v -> Log.e("Details Dialog", "Bookmarked"));
    }

    private void retrieveEventDetails() {
        MeetupService meetupService =
                RetrofitInstance.getMeetupRetrofitInstance().create(MeetupService.class);

        Call<MeetupEventDetails> meetupEventCall = meetupService.getMeetupEventDetails(groupId,
                eventId, API_KEY);
        meetupEventCall.enqueue(new Callback<MeetupEventDetails>() {
            @Override
            public void onResponse(Call<MeetupEventDetails> call,
                                   Response<MeetupEventDetails> response) {
                MeetupEventDetails meetupEventDetails = response.body();
                setEventInformation(meetupEventDetails);
            }

            @Override
            public void onFailure(Call<MeetupEventDetails> call, Throwable t) {
                //TODO: handle failure
            }
        });
    }

    private void setEventInformation(MeetupEventDetails meetupEventDetails) {
        String eventName = meetupEventDetails.getEventName();
        String groupName = meetupEventDetails.getEventGroupName().getEventGroupName();
        String status = "Status: " + meetupEventDetails.getEventStatus();

        int attendeesCount = meetupEventDetails.getEventAttendees();
        String attendeesCountString = String.valueOf(attendeesCount) + " members going";

        int waitlistCount = meetupEventDetails.getWaitlistCount();
        String waitlistCountString = String.valueOf(waitlistCount) + " members on waitlist";

        int maximumAttendees = meetupEventDetails.getMaximumAttendees();
        String placeLeftString = String.valueOf(maximumAttendees - attendeesCount) + " places left";

        MeetupEventLocation locationObject = meetupEventDetails.getLocationObject();
        String placeName = locationObject.getPlaceName();
        String address = locationObject.getAddress();
        String city = locationObject.getCity();
        String country = locationObject.getCountry();
        String addressString = placeName + ", " + address + ", " + city + ", " + country;

        String eventDate = meetupEventDetails.getEventDate();
        String eventTime = meetupEventDetails.getEventTime();
        String eventDescription = meetupEventDetails.getEventDescription();
        eventTitleTextView.setText(eventName);
        groupTextView.setText(groupName);
        statusTextView.setText(status);
        placeLeftTextView.setText(placeLeftString);
        attendeesTextView.setText(attendeesCountString);
        waitlistCountTextView.setText(waitlistCountString);
        dateTextView.setText(eventDate);
        timeTextView.setText(eventTime);
        addressTextView.setText(addressString);
        eventDescriptionTextView.setText(eventDescription);
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putString(KEY_GROUP_URL, groupId);
        currentState.putString(KEY_EVENT_ID, eventId);
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

