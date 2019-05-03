package com.example.dailyupdate.utilities.notifications;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.dailyupdate.utilities.Constants;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

public class JobUtilities {

    private static boolean sInitialized;

    synchronized public static void scheduleUpdateJob(@NonNull final Context context){
        if (sInitialized) return;
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Job constraintUpdateJob = dispatcher.newJobBuilder()
                .setService(UpdateFirebaseJobService.class)
                .setTag(Constants.UPDATE_JOB_TAG)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(Constants.UPDATE_INTERVAL_SECONDS,
                        Constants.UPDATE_INTERVAL_SECONDS + Constants.SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();
        dispatcher.schedule(constraintUpdateJob);

        sInitialized = true;
    }
}
