package com.example.dailyupdate.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyupdate.MainViewModel;
import com.example.dailyupdate.R;
import com.example.dailyupdate.data.model.MeetupEventDetails;
import com.example.dailyupdate.ui.adapter.BookmarksAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookmarksFragment extends Fragment {

    @BindView(R.id.main_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.main_emptyview) TextView emptyView;

    private MainViewModel viewModel;
    private BookmarksFragmentListener listener;
    private BookmarksAdapter bookmarksAdapter;

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
        View rootView = inflater.inflate(R.layout.main_layout, container, false);
        ButterKnife.bind(this, rootView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        bookmarksAdapter = new BookmarksAdapter();
        recyclerView.setAdapter(bookmarksAdapter);

        setBookmarkedEventsList();
        setDeleteSwipe();

        return rootView;
    }

    private void setBookmarkedEventsList() {
        viewModel.getAllBookmarkedEvents().observe(this, new Observer<List<MeetupEventDetails>>() {
            @Override
            public void onChanged(List<MeetupEventDetails> bookmarkedEventsList) {
                if (bookmarkedEventsList.size() < 1) {
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.setText(getString(R.string.bookmarks_emptyview_message));
                } else {
                    emptyView.setVisibility(View.GONE);
                    bookmarksAdapter.submitList(bookmarkedEventsList);
                    setAdapterClickListeners(bookmarkedEventsList);
                }
            }
        });
    }

    /**
     * Click on the bookmarked event item will open the event details,
     * and click on the bookmarks icon will delete the event from the bookmarks
     **/
    private void setAdapterClickListeners(List<MeetupEventDetails> bookmarkedEventsList) {
        // Open the event details
        bookmarksAdapter.setOnItemClickListener(new BookmarksAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                MeetupEventDetails bookmarkedEvent = bookmarkedEventsList.get(position);
                String eventId = bookmarkedEvent.getEventId();
                String groupUrl = bookmarkedEvent.getMeetupEventGroupName().getEventGroupUrl();
                listener.displayEventDetails(groupUrl, eventId);
            }
        });

        // Delete bookmarked event
        bookmarksAdapter.setOnBookmarkIconClickListener(new BookmarksAdapter.BookmarkIconClickListener() {
            @Override
            public void onBookmarkIconClick(MeetupEventDetails bookmarkedEvent) {
                viewModel.deleteBookmarkedEvent(bookmarkedEvent);
            }
        });
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