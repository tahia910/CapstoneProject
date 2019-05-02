package com.example.dailyupdate.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.dailyupdate.data.models.MeetupEvent;
import com.example.dailyupdate.data.models.MeetupEventDetails;
import com.example.dailyupdate.data.models.MeetupGroup;
import com.example.dailyupdate.repositories.MeetupRepository;

import java.util.List;

public class MeetupViewModel extends ViewModel {

    private MeetupRepository mRepo;

    public MeetupViewModel(){
        mRepo = MeetupRepository.getInstance();
    }

    public LiveData<List<MeetupGroup>> getMeetupGroupList(){
        return mRepo.getMeetupGroupList();
    }

    public LiveData<List<MeetupEvent>> getMeetupEventList(){
        return mRepo.getMeetupEventList();
    }

    public LiveData<MeetupEventDetails> getMeetupEventDetails(){return mRepo.getMeetupEventDetails();}

    public void searchMeetupGroups(String location, int category, int responsePageNumber){
        mRepo.searchMeetupGroups(location, category, responsePageNumber);
    }

    public void searchMeetupEvents(String location, String sortBy, int category,
                                   String searchKeyword){
        mRepo.searchMeetupEvents(location, sortBy, category, searchKeyword);
    }

    public void searchMeetupEventDetails(String groupId, String eventId){
        mRepo.searchMeetupEventDetails(groupId, eventId);
    }
}
