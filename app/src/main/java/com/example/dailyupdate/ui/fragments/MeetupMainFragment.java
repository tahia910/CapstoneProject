package com.example.dailyupdate.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.dailyupdate.R;
import com.example.dailyupdate.data.models.LatestSearch;
import com.example.dailyupdate.data.models.MeetupEvent;
import com.example.dailyupdate.data.models.MeetupEventDetails;
import com.example.dailyupdate.ui.adapters.MeetupEventAdapter;
import com.example.dailyupdate.utilities.Constants;
import com.example.dailyupdate.utilities.NetworkUtilities;
import com.example.dailyupdate.utilities.notifications.JobUtilities;
import com.example.dailyupdate.viewmodels.BookmarksDatabaseViewModel;
import com.example.dailyupdate.viewmodels.LatestSearchDatabaseViewModel;
import com.example.dailyupdate.viewmodels.MeetupViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeetupMainFragment extends Fragment {

    @BindView(R.id.main_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.main_layout) CoordinatorLayout mainLayout;
    @BindView(R.id.main_emptyview) TextView emptyView;
    @BindView(R.id.main_spinner) ProgressBar spinner;
    @BindView(R.id.main_swipe_refresh_layout) SwipeRefreshLayout refreshLayout;

    private String searchKeyword;
    private String sortBy;
    private String searchLocation;
    private SharedPreferences sharedPref;
    private MeetupMainFragmentListener listener;
    private MeetupEventAdapter meetupEventAdapter;
    private BookmarksDatabaseViewModel databaseViewModel;
    private MeetupViewModel meetupViewModel;
    private LatestSearchDatabaseViewModel searchCacheViewModel;
    private List<String> bookmarkedEventsListIds;
    private int recyclerViewLastPosition;

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

        databaseViewModel = new ViewModelProvider(requireActivity()).get(BookmarksDatabaseViewModel.class);
        meetupViewModel = new ViewModelProvider(requireActivity()).get(MeetupViewModel.class);
        searchCacheViewModel = new ViewModelProvider(this).get(LatestSearchDatabaseViewModel.class);
        subscribeAllObservers();
        getSharedPreferences(context);

        // Check if the network is available first, display empty view if there is no connection
        boolean isConnected = NetworkUtilities.checkNetworkAvailability(context);
        if (!isConnected) {
            spinner.setVisibility(View.GONE);
            recyclerView.setVisibility(View.INVISIBLE);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(R.string.no_internet_connection);
        } else {
            meetupViewModel.searchMeetupEvents(searchLocation, sortBy,
                    Constants.MEETUP_TECH_CATEGORY_NUMBER, searchKeyword);
        }
        return rootView;
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            recyclerViewLastPosition =
                    savedInstanceState.getInt(Constants.KEY_MEETUP_RECYCLERVIEW_POSITION);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        recyclerViewLastPosition =
                ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        outState.putInt(Constants.KEY_MEETUP_RECYCLERVIEW_POSITION, recyclerViewLastPosition);
    }

    /**
     * Observe the MeetupViewModel within the bookmarked events database ViewModel in order to
     * allow the user to insert new events in the database by clicking on the bookmark icon.
     * Also observe the cached search list within the MeetupViewModel, it will be used to warn
     * the user when there is any new event.
     **/
    private void subscribeAllObservers() {
        databaseViewModel.getAllBookmarkedEventsIds().observe(getViewLifecycleOwner(), strings -> {
            if (strings != null) {
                bookmarkedEventsListIds = strings;
                // If there is any change to the database (ex: the user bookmarked/deleted an
                // event from the bookmark database), verify that the adapter has been set up
                // first, then notify the adapter to update the UI
                if (meetupEventAdapter != null) {
                    // Before notifying the adapter of the change(s), save the current position
                    // in the RecyclerView
                    recyclerViewLastPosition =
                            ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                    meetupEventAdapter.notifyDataSetChanged();
                    // If the latest position in the RecyclerView was not at the top, scroll
                    // back to the previous position (restore state)
                    if (recyclerViewLastPosition != 0) {
                        ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPosition(recyclerViewLastPosition);
                    }
                }
            }

            meetupViewModel.getMeetupEventList().observe(getViewLifecycleOwner(),
                    meetupEventList -> {
                        spinner.setVisibility(View.GONE);
                        if (meetupEventList != null) {
                            meetupEventAdapter = new MeetupEventAdapter(getContext(),
                                    meetupEventList, bookmarkedEventsListIds);
                            recyclerView.setAdapter(meetupEventAdapter);
                            // If there was a screen rotation, restore the previous position
                            if (recyclerViewLastPosition != 0) {
                                ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPosition(recyclerViewLastPosition);
                            }

                            setNotifications(getContext());
                            setClickListeners(meetupEventList);
                            // Save the search to cache database
                            subscribeCachedDatabase(meetupEventList);
                        } else {
                            recyclerView.setVisibility(View.INVISIBLE);
                            emptyView.setVisibility(View.VISIBLE);
                            emptyView.setText(R.string.meetup_events_error_message);
                        }
                    });
        });
    }

    private void subscribeCachedDatabase(List<MeetupEvent> meetupEventList) {
        searchCacheViewModel.getAllLatestSearchLive().observe(getViewLifecycleOwner(),
                cachedEventsIds -> {
                    // If cached list is not empty, check if the list is the same
                    if (cachedEventsIds.size() > 1) {
                        List<String> currentListIds = new ArrayList<>();
                        for (int i = 0; i < meetupEventList.size(); i++) {
                            String currentEventId = meetupEventList.get(i).getEventId();
                            currentListIds.add(currentEventId);
                        }
                        // Compare last search IDs and current search IDs.
                        // Only the same items will be kept in the current search IDs.
                        // https://www.w3resource.com/java-tutorial/arraylist/arraylist_retainall.php
                        currentListIds.retainAll(cachedEventsIds);
                        if (currentListIds.size() != cachedEventsIds.size()) {
                            // Delete the previous search and insert the new one
                            searchCacheViewModel.deleteAllEvents();
                            insertNewCacheList(meetupEventList);
                        }
                    } else {
                        // The cached list was empty so put the current search items in the database
                        insertNewCacheList(meetupEventList);
                    }
                });
    }

    private void insertNewCacheList(List<MeetupEvent> meetupEventList) {
        List<LatestSearch> newCacheList = new ArrayList<>();
        for (int i = 0; i < meetupEventList.size(); i++) {
            LatestSearch item = new LatestSearch();
            String currentEventId = meetupEventList.get(i).getEventId();
            item.setEventId(currentEventId);
            newCacheList.add(item);
        }
        searchCacheViewModel.insertLatestSearch(newCacheList);
    }

    private void setClickListeners(List<MeetupEvent> meetupEventList) {
        // Open the details of the selected event
        meetupEventAdapter.setOnItemClickListener((position, v) -> {
            MeetupEvent meetupEvent = meetupEventList.get(position);
            String eventId = meetupEvent.getEventId();
            String groupUrl = meetupEvent.getGroupNameObject().getEventGroupUrl();
            listener.currentEventInfo(groupUrl, eventId);
        });

        // Add the selected event to the database
        meetupEventAdapter.setOnBookmarkIconClickListener((currentEvent, currentPosition) -> {
            String currentEventId = currentEvent.getEventId();
            MeetupEventDetails bookmarkEvent = new MeetupEventDetails();
            bookmarkEvent.setEventId(currentEventId);
            // If the item is already in database, delete it
            // Then notify the adapter to update the bookmark icon
            if (bookmarkedEventsListIds.contains(currentEventId)) {
                databaseViewModel.deleteBookmarkedEvent(bookmarkEvent);
                meetupEventAdapter.notifyItemChanged(currentPosition);
            } else {
                bookmarkEvent.setEventId(currentEventId);
                bookmarkEvent.setEventName(currentEvent.getEventName());
                bookmarkEvent.setMeetupEventGroupName(currentEvent.getGroupNameObject());
                bookmarkEvent.setEventDate(currentEvent.getEventDate());
                bookmarkEvent.setEventTime(currentEvent.getEventTime());
                databaseViewModel.insertBookmarkedEvent(bookmarkEvent);
                meetupEventAdapter.notifyItemChanged(currentPosition);
            }
        });

        // Set the swipe action to refresh the search
        refreshLayout.setOnRefreshListener(() -> {
            meetupViewModel.searchMeetupEvents(searchLocation, sortBy,
                    Constants.MEETUP_TECH_CATEGORY_NUMBER, searchKeyword);
            refreshLayout.setRefreshing(false);
        });
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
                    .setAction(getString(R.string.notification_snackbar_action), view -> {
                        JobUtilities.scheduleUpdateJob(context);
                        sharedPref.edit().putBoolean(getString(R.string.pref_notification_key), true).apply();
                        Toast.makeText(context,
                                getString(R.string.toast_notification_set_confirmation),
                                Toast.LENGTH_LONG).show();
                    });
            snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
            snackbar.show();
        }
    }

}
