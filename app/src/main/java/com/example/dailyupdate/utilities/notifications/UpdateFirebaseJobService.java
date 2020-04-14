package com.example.dailyupdate.utilities.notifications;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.example.dailyupdate.repositories.LatestSearchDatabaseRepository;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.ArrayList;
import java.util.List;

public class UpdateFirebaseJobService extends JobService {

    private AsyncTask mBackgroundTask;
    private List<String> events;

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        mBackgroundTask = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                // Fetch the events saved during the latest search
                LatestSearchDatabaseRepository repo =
                        new LatestSearchDatabaseRepository(getApplication());
                events = repo.getAllLatestSearch();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                // Make an ArrayList of the event IDs from the previous search
                ArrayList<String> latestSearchIds = new ArrayList<>(events);
                // Retrieve the current events available by querying the Meetup API on another
                // thread
                // Compare both events lists IDs and send a notification if there is any new event
                JobTasks.searchForNewEvents(getApplicationContext(), latestSearchIds);

                // Call jobFinished() when the task is done (set boolean to "false" because we
                // don't need to reschedule the job as it is completed here)
                jobFinished(jobParameters, false);
            }
        };
        mBackgroundTask.execute();

        // Return true because job is still doing something on another thread
        return true;
    }

    /**
     * This method gets called when requirements are no longer met while doing the job
     **/
    @Override
    public boolean onStopJob(JobParameters job) {
        // Cancel job
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
        // Return true because the job should be started again when conditions are re-met
        return true;
    }
}
