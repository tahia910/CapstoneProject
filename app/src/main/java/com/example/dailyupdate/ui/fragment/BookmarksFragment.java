package com.example.dailyupdate.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyupdate.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookmarksFragment extends Fragment {

    @BindView(R.id.main_recycler_view)
    RecyclerView recyclerView;

    public static BookmarksFragment newInstance() {
        return new BookmarksFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_layout, container, false);
        ButterKnife.bind(this, rootView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        MeetupEventAdapter meetupEventAdapter = new MeetupEventAdapter(getContext(),
//                meetupEventList);
//        recyclerView.setAdapter(meetupEventAdapter);
        return rootView;
    }
}
