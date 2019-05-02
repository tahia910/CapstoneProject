package com.example.dailyupdate.networking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.dailyupdate.data.models.MeetupGroup;
import com.example.dailyupdate.utilities.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class MeetupGroupApiClient {

    private static MeetupGroupApiClient instance;
    private MutableLiveData<List<MeetupGroup>> meetupGroupList;

    public static MeetupGroupApiClient getInstance() {
        if (instance == null) {
            instance = new MeetupGroupApiClient();
        }
        return instance;
    }

    private MeetupGroupApiClient() {
        meetupGroupList = new MutableLiveData<>();
    }

    public LiveData<List<MeetupGroup>> getMeetupGroups() {
        return meetupGroupList;
    }

    private Call<List<MeetupGroup>> getMeetupRetrofitResponse(String location, int category,
                                                              int responsePageNumber) {
        return RetrofitInstance.getMeetupService().getMeetupGroupList(Constants.MEETUP_API_KEY,
                location, category, responsePageNumber);
    }
    public void queryMeetupApiForGroups(String location, int category,
                                        int responsePageNumber) {
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = getMeetupRetrofitResponse(location, category, responsePageNumber).execute();
                    if (response.code() == 200) {
                        List<MeetupGroup> responseList = (List<MeetupGroup>) response.body();
                        meetupGroupList.postValue(responseList);
                    } else {
                        meetupGroupList.postValue(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    meetupGroupList.postValue(null);
                }
            }
        });
    }


}
