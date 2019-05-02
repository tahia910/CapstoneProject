package com.example.dailyupdate.networking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.dailyupdate.data.models.MeetupEventDetails;
import com.example.dailyupdate.utilities.Constants;

import retrofit2.Call;
import retrofit2.Response;

public class MeetupDetailsApiClient {

    private static MeetupDetailsApiClient instance;
    private MutableLiveData<MeetupEventDetails> meetupEventDetails;

    public static MeetupDetailsApiClient getInstance() {
        if (instance == null) {
            instance = new MeetupDetailsApiClient();
        }
        return instance;
    }

    private MeetupDetailsApiClient() {
        meetupEventDetails = new MutableLiveData<>();
    }

    public LiveData<MeetupEventDetails> getEventDetails() {
        return meetupEventDetails;
    }

    private Call<MeetupEventDetails> getMeetupRetrofitResponse(String groupId, String eventId) {
        return RetrofitInstance.getMeetupService().getMeetupEventDetails(groupId, eventId,
                Constants.MEETUP_API_KEY);
    }

    public void queryMeetupApiForEventDetails(String groupId, String eventId) {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = getMeetupRetrofitResponse(groupId, eventId).execute();
                    if (response.code() == 200) {
                        MeetupEventDetails eventDetailsResponse =
                                (MeetupEventDetails) response.body();
                        meetupEventDetails.postValue(eventDetailsResponse);
                    } else {
                        meetupEventDetails.postValue(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    meetupEventDetails.postValue(null);
                }
            }
        });
    }
}
