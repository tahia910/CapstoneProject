package com.example.dailyupdate.utilities;

import android.content.Context;

import androidx.annotation.NonNull;

import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class JobUtilities {

    // TODO: change this variable depending on the user's preference
    private static final int UPDATE_INTERVAL_HOURS = 3;
    private static final int UPDATE_INTERVAL_SECONDS =
            (int) (TimeUnit.HOURS.toSeconds(UPDATE_INTERVAL_HOURS));
    private static final int SYNC_FLEXTIME_SECONDS = UPDATE_INTERVAL_SECONDS;
    private static final String UPDATE_JOB_TAG = "search_update_tag";
    private static boolean sInitialized;

    synchronized public static void scheduleUpdateJob(@NonNull final Context context){
        if (sInitialized) return;
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Job constraintUpdateJob = dispatcher.newJobBuilder()
                .setService(UpdateFirebaseJobService.class)
                .setTag(UPDATE_JOB_TAG)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(UPDATE_INTERVAL_SECONDS,
                        UPDATE_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();
        dispatcher.schedule(constraintUpdateJob);

        sInitialized = true;
    }
}
