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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyupdate.R;
import com.example.dailyupdate.data.model.MeetupEvent;
import com.example.dailyupdate.data.model.MeetupEventDetails;
import com.example.dailyupdate.ui.adapter.MeetupEventAdapter;
import com.example.dailyupdate.utilities.Constants;
import com.example.dailyupdate.utilities.notifications.JobUtilities;
import com.example.dailyupdate.viewmodels.BookmarksDatabaseViewModel;
import com.example.dailyupdate.viewmodels.MeetupViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeetupMainFragment extends Fragment {

    @BindView(R.id.main_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.main_layout) CoordinatorLayout mainLayout;
    @BindView(R.id.main_emptyview) TextView emptyView;
    @BindView(R.id.main_spinner) ProgressBar spinner;

    private String searchKeyword;
    private String sortBy;
    private String searchLocation;
    private SharedPreferences sharedPref;
    private MeetupMainFragmentListener listener;
    private MeetupEventAdapter meetupEventAdapter;
    private BookmarksDatabaseViewModel databaseViewModel;
    private MeetupViewModel meetupViewModel;

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

        databaseViewModel = ViewModelProviders.of(this).get(BookmarksDatabaseViewModel.class);
        meetupViewModel = ViewModelProviders.of(this).get(MeetupViewModel.class);
        subscribeMeetupEventObserver();

        getSharedPreferences(context);
        meetupViewModel.searchMeetupEvents(searchLocation, sortBy, Constants.MEETUP_TECH_CATEGORY_NUMBER, searchKeyword);
        return rootView;
    }

    /**
     * Get the search criterias stocked in the SharedPreferences
     **/
    private void getSharedPreferences(Context context) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        searchKeyword = sharedPref.getString(getString(R.string.pref_meetup_edittext_key), "");
        sortBy = sharedPref.getString(getString(R.string.pref_meetup_sort_key),
                getString(R.string.pref_meetup_sort_default));
        searchLocation = sharedPref.getString(getString(R.string.pref_meetup_location_key), "");
    }

    private void subscribeMeetupEventObserver() {
        meetupViewModel.getMeetupEventList().observe(this, new Observer<List<MeetupEvent>>() {
            @Override
            public void onChanged(List<MeetupEvent> meetupEventList) {
                if (meetupEventList != null) {
                    spinner.setVisibility(View.GONE);
                    meetupEventAdapter = new MeetupEventAdapter(getContext(), meetupEventList);
                    recyclerView.setAdapter(meetupEventAdapter);
                    setNotifications(getContext());

                    // Open the details of the selected event
                    meetupEventAdapter.setOnItemClickListener((position, v) -> {
                        MeetupEvent meetupEvent = meetupEventList.get(position);
                        String eventId = meetupEvent.getEventId();
                        String groupUrl = meetupEvent.getGroupNameObject().getEventGroupUrl();
                        listener.currentEventInfo(groupUrl, eventId);
                    });

                    // Add the selected event to the database
                    meetupEventAdapter.setOnBookmarkIconClickListener(currentEvent -> {
                        String currentEventId = currentEvent.getEventId();
                        MeetupEventDetails bookmarkEvent = new MeetupEventDetails();
                        bookmarkEvent.setEventId(currentEventId);
                        bookmarkEvent.setEventName(currentEvent.getEventName());
                        bookmarkEvent.setMeetupEventGroupName(currentEvent.getGroupNameObject());
                        bookmarkEvent.setEventDate(currentEvent.getEventDate());
                        bookmarkEvent.setEventTime(currentEvent.getEventTime());
                        databaseViewModel.insertBookmarkedEvent(bookmarkEvent);
                    });
                }
            }
        });
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
                    getString(R.string.notification_snackbar_label), Snackbar.LENGTH_LONG).setAction(getString(R.string.notification_snackbar_action), new View.OnClickListener() {
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


}
