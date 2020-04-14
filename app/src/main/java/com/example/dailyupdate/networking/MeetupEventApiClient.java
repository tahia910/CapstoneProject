package com.example.dailyupdate.networking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.dailyupdate.data.models.MeetupEvent;
import com.example.dailyupdate.data.models.MeetupEventResponse;
import com.example.dailyupdate.utilities.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class MeetupEventApiClient {
    private static MeetupEventApiClient instance;
    private MutableLiveData<List<MeetupEvent>> meetupEventList;

    public static MeetupEventApiClient getInstance() {
        if (instance == null) {
            instance = new MeetupEventApiClient();
        }
        return instance;
    }

    private MeetupEventApiClient() {
        meetupEventList = new MutableLiveData<>();
    }

    public LiveData<List<MeetupEvent>> getMeetupEvents() {
        return meetupEventList;
    }

    private Call<MeetupEventResponse> getMeetupRetrofitResponse(String location, String sortBy,
                                                                int category,
                                                                String searchKeyword) {
        return RetrofitInstance.getMeetupService().getMeetupEventList(Constants.MEETUP_API_KEY,
                location, sortBy, category, searchKeyword);
    }

    public void queryMeetupApiForEvents(String location, String sortBy, int category,
                                        String searchKeyword) {
        AppExecutors.getInstance().networkIO().execute(() -> {
            try {
                Response response = getMeetupRetrofitResponse(location, sortBy, category,
                        searchKeyword).execute();
                if (response.code() == 200) {
                    MeetupEventResponse meetupEventResponse =
                            (MeetupEventResponse) response.body();
                    List<MeetupEvent> responseList = meetupEventResponse.getMeetupEventsList();
                    meetupEventList.postValue(responseList);
                } else {
                    meetupEventList.postValue(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                meetupEventList.postValue(null);
            }
        });
    }
}
