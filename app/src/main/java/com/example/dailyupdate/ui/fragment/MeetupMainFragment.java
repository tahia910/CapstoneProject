package com.example.dailyupdate.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyupdate.BuildConfig;
import com.example.dailyupdate.R;
import com.example.dailyupdate.data.MeetupEvent;
import com.example.dailyupdate.data.MeetupEventResponse;
import com.example.dailyupdate.data.MeetupGroup;
import com.example.dailyupdate.networking.MeetupService;
import com.example.dailyupdate.networking.RetrofitInstance;
import com.example.dailyupdate.ui.adapter.MeetupEventAdapter;
import com.example.dailyupdate.ui.adapter.MeetupGroupAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.dailyupdate.ui.fragment.MeetupDialogFragment.KEY_KEYWORD;
import static com.example.dailyupdate.ui.fragment.MeetupDialogFragment.KEY_LOCATION;
import static com.example.dailyupdate.ui.fragment.MeetupDialogFragment.KEY_SORT_BY;

public class MeetupMainFragment extends Fragment {

    @BindView(R.id.main_recycler_view)
    RecyclerView recyclerView;
    private String userLocation;
    private String defaultLocation = "tokyo";
    private int searchCategoryNumber = 34; // Category "Tech"
    private String API_KEY = BuildConfig.MEETUP_API_KEY;
    private String searchKeyword = "android";

    private String sortBy;
    private String searchLocation;

    public MeetupMainFragment() {
    }

    public static MeetupMainFragment newInstance(Bundle searchArguments) {
        MeetupMainFragment meetupMainFragment = new MeetupMainFragment();
        Bundle args = new Bundle();
        args.putBundle("KEY_SEARCH_ARGUMENTS", searchArguments);
        meetupMainFragment.setArguments(args);
        return meetupMainFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_layout, container, false);
        ButterKnife.bind(this, rootView);

        Bundle bundle = getArguments().getParcelable("KEY_SEARCH_ARGUMENTS");
        if (!bundle.isEmpty()) {
            searchKeyword = bundle.getString(KEY_KEYWORD);
            sortBy = bundle.getString(KEY_SORT_BY);
            searchLocation = bundle.getString(KEY_LOCATION);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (sortBy.equals("groups")) {
            retrieveMeetupGroups();
        } else if (sortBy.equals("events")) {
            retrieveMeetupEvents();
        }
        return rootView;
    }

    private void retrieveMeetupGroups() {
        MeetupService meetupService =
                RetrofitInstance.getMeetupRetrofitInstance().create(MeetupService.class);
        Call<List<MeetupGroup>> meetupGroupCall =
                meetupService.getMeetupGroupListWithKeywords(API_KEY, userLocation,
                        searchCategoryNumber, searchKeyword);
        meetupGroupCall.enqueue(new Callback<List<MeetupGroup>>() {
            @Override
            public void onResponse(Call<List<MeetupGroup>> call,
                                   Response<List<MeetupGroup>> response) {
                List<MeetupGroup> meetupGroupList = response.body();

                MeetupGroupAdapter meetupGroupAdapter = new MeetupGroupAdapter(getContext(),
                        meetupGroupList, 2);
                recyclerView.setAdapter(meetupGroupAdapter);
            }

            @Override
            public void onFailure(Call<List<MeetupGroup>> call, Throwable t) {
                //TODO: handle failure
            }
        });
    }

    private void retrieveMeetupEvents() {

        MeetupService meetupService =
                RetrofitInstance.getMeetupRetrofitInstance().create(MeetupService.class);

        Call<MeetupEventResponse> meetupEventCall = meetupService.getMeetupEventList(API_KEY,
                userLocation, searchCategoryNumber, searchKeyword);
        meetupEventCall.enqueue(new Callback<MeetupEventResponse>() {
            @Override
            public void onResponse(Call<MeetupEventResponse> call,
                                   Response<MeetupEventResponse> response) {
                MeetupEventResponse meetupEventResponse = response.body();
                List<MeetupEvent> meetupEventList = meetupEventResponse.getMeetupEventsList();

                MeetupEventAdapter meetupEventAdapter = new MeetupEventAdapter(getContext(),
                        meetupEventList);
                recyclerView.setAdapter(meetupEventAdapter);
            }

            @Override
            public void onFailure(Call<MeetupEventResponse> call, Throwable t) {
                //TODO: handle failure
            }
        });
    }

}
