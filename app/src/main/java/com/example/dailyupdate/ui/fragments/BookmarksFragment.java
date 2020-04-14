package com.example.dailyupdate.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyupdate.R;
import com.example.dailyupdate.data.models.MeetupEventDetails;
import com.example.dailyupdate.ui.adapters.BookmarksAdapter;
import com.example.dailyupdate.utilities.Constants;
import com.example.dailyupdate.viewmodels.BookmarksDatabaseViewModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookmarksFragment extends Fragment {

    @BindView(R.id.bookmarks_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.bookmarks_emptyview) TextView emptyView;
    @BindView(R.id.bookmarks_adview) AdView adView;

    private BookmarksDatabaseViewModel viewModel;
    private BookmarksFragmentListener listener;
    private BookmarksAdapter bookmarksAdapter;
    private int recyclerViewLastPosition;

    public interface BookmarksFragmentListener {
        void displayEventDetails(String groupUrl, String eventId);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BookmarksFragmentListener) {
            listener = (BookmarksFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement " +
                    "BookmarksFragmentListener");
        }
    }

    public static BookmarksFragment newInstance() {
        return new BookmarksFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.bookmarks_layout, container, false);
        ButterKnife.bind(this, rootView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        viewModel = new ViewModelProvider(this).get(BookmarksDatabaseViewModel.class);
        bookmarksAdapter = new BookmarksAdapter();
        recyclerView.setAdapter(bookmarksAdapter);

        // If there was a screen rotation, restore the previous position
        if (recyclerViewLastPosition != 0) {
            ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPosition(recyclerViewLastPosition);
        }
        setAdsBanner();
        setBookmarkedEventsList();
        setDeleteSwipe();

        return rootView;
    }

    /**
     * Load the test ad to display into the ApView banner
     **/
    private void setAdsBanner() {
        AdRequest adRequest =
                new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                Toast.makeText(getContext(),
                        getString(R.string.bookmarks_ad_banner_onclick_message),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            recyclerViewLastPosition =
                    savedInstanceState.getInt(Constants.KEY_BOOKMARK_RECYCLERVIEW_POSITION);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        recyclerViewLastPosition =
                ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        outState.putInt(Constants.KEY_BOOKMARK_RECYCLERVIEW_POSITION, recyclerViewLastPosition);
    }


    private void setBookmarkedEventsList() {
        viewModel.getAllBookmarkedEvents().observe(getViewLifecycleOwner(), (Observer<List<MeetupEventDetails>>) bookmarkedEventsList -> {
            if (bookmarkedEventsList.size() < 1) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText(getString(R.string.bookmarks_emptyview_message));
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                bookmarksAdapter.submitList(bookmarkedEventsList);

                // If there was a screen rotation, restore the previous position
                if (recyclerViewLastPosition != 0) {
                    ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPosition(recyclerViewLastPosition);
                }

                setAdapterClickListeners(bookmarkedEventsList);
            }
        });
    }

    /**
     * Click on the bookmarked event item will open the event details,
     * and click on the bookmarks icon will delete the event from the bookmarks
     **/
    private void setAdapterClickListeners(List<MeetupEventDetails> bookmarkedEventsList) {
        // Open the event details
        bookmarksAdapter.setOnItemClickListener((position, v) -> {
            MeetupEventDetails bookmarkedEvent = bookmarkedEventsList.get(position);
            String eventId = bookmarkedEvent.getEventId();
            String groupUrl = bookmarkedEvent.getMeetupEventGroupName().getEventGroupUrl();
            listener.displayEventDetails(groupUrl, eventId);
        });

        // Delete bookmarked event
        bookmarksAdapter.setOnBookmarkIconClickListener(bookmarkedEvent ->
                viewModel.deleteBookmarkedEvent(bookmarkedEvent));
    }

    /**
     * Swipe right or left will delete the bookmarked event from the bookmarks database
     **/
    private void setDeleteSwipe() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                viewModel.deleteBookmarkedEvent(bookmarksAdapter.getCurrentEvent(viewHolder.getAdapterPosition()));
            }
        }).attachToRecyclerView(recyclerView);
    }

}