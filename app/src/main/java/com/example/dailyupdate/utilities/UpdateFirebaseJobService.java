package com.example.dailyupdate.utilities;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class UpdateFirebaseJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        // TODO: Make the search on another thread, check for new events compared to last search (?)

        //Call when the task is done ("false" because don't need to reschedule)
        jobFinished(jobParameters, false);

        // TODO: Display notification with AppService.ACTION_GET_UPDATE_NOTIFICATION
        // depending on the result

        // Return true because job is still doing something on another thread
        return true;
    }

    /**
     * This method gets called when requirements are no longer met
     **/
    @Override
    public boolean onStopJob(JobParameters job) {
        // Cancel job
        // Return true because the job should be started again when conditions are re-met
        return true;
    }
}
