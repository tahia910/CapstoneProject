package com.example.dailyupdate.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyupdate.R;
import com.example.dailyupdate.data.models.MeetupEventDetails;
import com.example.dailyupdate.utilities.DateUtilities;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * BookmarksAdapter extends the RecyclerView's ListAdapter in order to use
 * DiffUtil/AsyncListDiffer, that will check the difference between old and new data passed in the
 * LiveData, then update the RecyclerView.
 * <p>
 * https://developer.android.com/reference/kotlin/androidx/recyclerview/widget/ListAdapter
 * https://developer.android.com/reference/kotlin/androidx/recyclerview/widget/DiffUtil.html
 * https://developer.android.com/reference/kotlin/androidx/recyclerview/widget/AsyncListDiffer.html
 * https://www.youtube.com/watch?v=xPPMygGxiEo
 **/
public class BookmarksAdapter extends ListAdapter<MeetupEventDetails,
        BookmarksAdapter.BookmarksAdapterViewHolder> {

    @BindView(R.id.textview_event_title) TextView eventNameTextView;
    @BindView(R.id.textview_event_date) TextView eventDateTextView;
    @BindView(R.id.textview_event_time) TextView eventTimeTextView;
    @BindView(R.id.textview_group_title) TextView eventGroupTextView;
    @BindView(R.id.bookmarks_icon_bookmarks_item) ImageView bookmarkIcon;

    private static ClickListener clickListener;
    private static BookmarkIconClickListener bookmarkIconListener;

    public BookmarksAdapter() {
        super(DIFF_CALLBACK);
    }

    // Make the variable static so it can be passed to the super class constructor before
    // the adapter constructor is finished.
    // (If non-static, can't be accessed before the adapter instance is created, so
    // wouldn't be available in constructor.)
    private static final DiffUtil.ItemCallback<MeetupEventDetails> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<MeetupEventDetails>() {
        @Override
        public boolean areItemsTheSame(@NonNull MeetupEventDetails oldItem,
                                       @NonNull MeetupEventDetails newItem) {
            return oldItem.getEventId().equals(newItem.getEventId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull MeetupEventDetails oldItem,
                                          @NonNull MeetupEventDetails newItem) {
            // Must check if all values are the same in order to make sure it is the same item
            // Can't use oldItem.equals(newItem) as it will always be false (different Java object)
            return oldItem.getEventName().equals(newItem.getEventName())
                    && oldItem.getMeetupEventGroupName().getEventGroupUrl()
                    .equals(newItem.getMeetupEventGroupName().getEventGroupUrl())
                    && oldItem.getEventTime().equals(newItem.getEventTime())
                    && oldItem.getEventDate().equals(newItem.getEventDate());
        }
    };

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        BookmarksAdapter.clickListener = clickListener;
    }

    public interface BookmarkIconClickListener {
        void onBookmarkIconClick(MeetupEventDetails bookmarkedEvent);
    }

    public void setOnBookmarkIconClickListener(BookmarkIconClickListener bookmarkIconListener) {
        BookmarksAdapter.bookmarkIconListener = bookmarkIconListener;
    }

    @Override
    public BookmarksAdapterViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmarks_item,
                parent, false);
        ButterKnife.bind(this, view);
        return new BookmarksAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookmarksAdapterViewHolder viewHolder, int i) {
        MeetupEventDetails bookmarkedEvent = getItem(i);
        String dateWithDay = DateUtilities.getDateWithDay(bookmarkedEvent.getEventDate());
        String formattedTime = DateUtilities.getFormattedTime(bookmarkedEvent.getEventTime());
        String eventGroupName = bookmarkedEvent.getMeetupEventGroupName().getEventGroupName();

        eventNameTextView.setText(bookmarkedEvent.getEventName());
        eventDateTextView.setText(dateWithDay);
        eventTimeTextView.setText(formattedTime);
        eventGroupTextView.setText(eventGroupName);
    }

    public MeetupEventDetails getCurrentEvent(int position) {
        return getItem(position);
    }

    public class BookmarksAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, BookmarkIconClickListener {

        private BookmarksAdapterViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            bookmarkIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (bookmarkIconListener != null && position != RecyclerView.NO_POSITION) {
                        bookmarkIconListener.onBookmarkIconClick(getItem(position));
                    }
                }
            });
        }

        @Override
        public void onClick(final View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

        @Override
        public void onBookmarkIconClick(MeetupEventDetails bookmarkedEvent) {
            bookmarkIconListener.onBookmarkIconClick(getCurrentEvent(getAdapterPosition()));
        }
    }
}
