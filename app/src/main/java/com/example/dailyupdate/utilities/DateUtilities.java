package com.example.dailyupdate.utilities;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtilities {

    /**
     * Format a given date string to add the day to it
     **/
    public static String getDateWithDay(String eventDateValue) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM" + "-dd");
        Date dateFormat = new Date();
        try {
            dateFormat = format.parse(eventDateValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateFormat.format("EEEE, MMM dd", dateFormat).toString();
    }

    /**
     * Format a given time string to add AM/PM to it
     **/
    public static String getFormattedTime(String timeValue) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm");
        Date timeFormat = new Date();
        try {
            timeFormat = format.parse(timeValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateFormat.format("hh:mm aaa", timeFormat).toString();
    }
}
