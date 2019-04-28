package com.example.dailyupdate.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyupdate.R;
import com.example.dailyupdate.data.model.MeetupEventDetails;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.BookmarksAdapterViewHolder> {

    @BindView(R.id.textview_event_title)
    TextView eventNameTextView;
    @BindView(R.id.textview_event_date)
    TextView eventDateTextView;
    @BindView(R.id.textview_event_time)
    TextView eventTimeTextView;
    @BindView(R.id.textview_group_title)
    TextView eventGroupTextView;
    @BindView(R.id.bookmarks_icon_bookmarks_item)
    ImageView bookmarkIcon;

    private Context context;
    private static ClickListener clickListener;
    private static BookmarkIconClickListener bookmarkIconListener;
    private final List<MeetupEventDetails> bookmarkedEventList;

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

    public BookmarksAdapter(Context context, List<MeetupEventDetails> bookmarkedEventList) {
        this.context = context;
        this.bookmarkedEventList = bookmarkedEventList;
    }

    @Override
    public BookmarksAdapterViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.bookmarks_item, parent, false);
        ButterKnife.bind(this, view);
        return new BookmarksAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookmarksAdapterViewHolder viewHolder, int i) {
        MeetupEventDetails bookmarkedEvent = bookmarkedEventList.get(i);
        // TODO: change date & time format
        String eventDate = bookmarkedEvent.getEventDate();
        String eventTime = bookmarkedEvent.getEventTime();
        String eventGroupName = bookmarkedEvent.getMeetupEventGroupName().getEventGroupName();

        eventNameTextView.setText(bookmarkedEvent.getEventName());
        eventDateTextView.setText(eventDate);
        eventTimeTextView.setText(eventTime);
        eventGroupTextView.setText(eventGroupName);
    }

    @Override
    public int getItemCount() {
        if (null == bookmarkedEventList) return 0;
        return bookmarkedEventList.size();
    }

    public MeetupEventDetails getCurrentEvent(int position) {
        return bookmarkedEventList.get(position);
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
                        bookmarkIconListener.onBookmarkIconClick(bookmarkedEventList.get(position));
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
