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
import com.example.dailyupdate.data.MeetupGroup;
import com.example.dailyupdate.networking.MeetupRetrofitInstance;
import com.example.dailyupdate.networking.MeetupService;
import com.example.dailyupdate.ui.adapter.MeetupGroupAdapter;

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

    @BindView(R.id.meetup_main_recycler_view)
    RecyclerView recyclerView;
    private String userLocation;
    private String defaultLocation = "tokyo";
    private int meetupGroupCategoryNumber = 34; // Category "Tech"
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

        retrieveMeetupGroups();

        return rootView;
    }

    private void retrieveMeetupGroups() {
        MeetupService meetupService =
                MeetupRetrofitInstance.getMeetupRetrofitInstance().create(MeetupService.class);
        Call<List<MeetupGroup>> meetupGroupCall =
                meetupService.getMeetupGroupListWithKeywords(API_KEY, userLocation,
                        meetupGroupCategoryNumber, searchKeyword);
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

}
