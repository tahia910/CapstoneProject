package com.example.dailyupdate.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyupdate.BuildConfig;
import com.example.dailyupdate.MainViewModel;
import com.example.dailyupdate.R;
import com.example.dailyupdate.data.model.MeetupEvent;
import com.example.dailyupdate.data.model.MeetupEventDetails;
import com.example.dailyupdate.data.model.MeetupEventResponse;
import com.example.dailyupdate.networking.MeetupService;
import com.example.dailyupdate.networking.RetrofitInstance;
import com.example.dailyupdate.ui.adapter.MeetupEventAdapter;
import com.example.dailyupdate.utilities.JobUtilities;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetupMainFragment extends Fragment {

    @BindView(R.id.main_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.main_layout) CoordinatorLayout mainLayout;
    @BindView(R.id.main_emptyview) TextView emptyView;
    @BindView(R.id.main_spinner) ProgressBar spinner;

    private int searchCategoryNumber = 34; // Category "Tech"
    private String API_KEY = BuildConfig.MEETUP_API_KEY;
    private String searchKeyword;
    private String sortBy;
    private String searchLocation;
    private SharedPreferences sharedPref;
    private MeetupMainFragmentListener listener;
    private MeetupEventAdapter meetupEventAdapter;
    private MainViewModel viewModel;

    public interface MeetupMainFragmentListener {
        void currentEventInfo(String groupUrl, String eventId);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MeetupMainFragmentListener) {
            listener = (MeetupMainFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement " +
                    "MeetupMainFragmentListener");
        }
    }

    public static MeetupMainFragment newInstance() {
        return new MeetupMainFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_layout, container, false);
        ButterKnife.bind(this, rootView);
        Context context = getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        spinner.setVisibility(View.VISIBLE);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        searchKeyword = sharedPref.getString(getString(R.string.pref_meetup_edittext_key), "");
        sortBy = sharedPref.getString(getString(R.string.pref_meetup_sort_key),
                getString(R.string.pref_meetup_sort_default));
        searchLocation = sharedPref.getString(getString(R.string.pref_meetup_location_key), "");

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        retrieveMeetupEvents();
        setNotifications(context);
        return rootView;
    }

    /**
     * Check the preferences to see if the user wants to get notifications on events search
     **/
    private void setNotifications(Context context) {
        boolean notificationSwitchValue =
                sharedPref.getBoolean(getString(R.string.pref_notification_key), false);
        if (notificationSwitchValue) {
            JobUtilities.scheduleUpdateJob(context);
        } else {
            // Display a Snackbar to ask if the user wants to get notifications for current search
            Snackbar snackbar = Snackbar.make(mainLayout,
                    getString(R.string.notification_snackbar_label), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.notification_snackbar_action), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JobUtilities.scheduleUpdateJob(context);
                    sharedPref.edit().putBoolean(getString(R.string.pref_notification_key), true).apply();
                    Toast.makeText(context,
                            getString(R.string.toast_notification_set_confirmation),
                            Toast.LENGTH_LONG).show();
                }
            });
            snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
            snackbar.show();
        }
    }

    private void retrieveMeetupEvents() {
        MeetupService meetupService =
                RetrofitInstance.getMeetupRetrofitInstance().create(MeetupService.class);

        Call<MeetupEventResponse> meetupEventCall = meetupService.getMeetupEventList(API_KEY,
                searchLocation, sortBy, searchCategoryNumber, searchKeyword);
        meetupEventCall.enqueue(new Callback<MeetupEventResponse>() {
            @Override
            public void onResponse(Call<MeetupEventResponse> call,
                                   Response<MeetupEventResponse> response) {
                spinner.setVisibility(View.GONE);
                MeetupEventResponse meetupEventResponse = response.body();
                List<MeetupEvent> meetupEventList = meetupEventResponse.getMeetupEventsList();

                meetupEventAdapter = new MeetupEventAdapter(getContext(), meetupEventList);
                recyclerView.setAdapter(meetupEventAdapter);

                meetupEventAdapter.setOnItemClickListener((position, v) -> {
                    MeetupEvent meetupEvent = meetupEventList.get(position);
                    String eventId = meetupEvent.getEventId();
                    String groupUrl = meetupEvent.getGroupNameObject().getEventGroupUrl();
                    listener.currentEventInfo(groupUrl, eventId);
                });

                meetupEventAdapter.setOnBookmarkIconClickListener(new MeetupEventAdapter.BookmarkIconClickListener() {
                    @Override
                    public void onBookmarkIconClick(MeetupEvent currentEvent) {

                        String currentEventId = currentEvent.getEventId();

                        MeetupEventDetails bookmarkEvent = new MeetupEventDetails();
                        bookmarkEvent.setEventId(currentEventId);
                        bookmarkEvent.setEventName(currentEvent.getEventName());
                        bookmarkEvent.setMeetupEventGroupName(currentEvent.getGroupNameObject());
                        bookmarkEvent.setEventDate(currentEvent.getEventDate());
                        bookmarkEvent.setEventTime(currentEvent.getEventTime());
                        viewModel.insertBookmarkedEvent(bookmarkEvent);
                    }
                });
            }

            @Override
            public void onFailure(Call<MeetupEventResponse> call, Throwable t) {
                spinner.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText(getString(R.string.meetup_events_error_message));
            }
        });
    }

}
