package com.example.dailyupdate.utilities;

import com.example.dailyupdate.BuildConfig;

import java.util.concurrent.TimeUnit;

public class Constants {

    // API request related
    public static final String GITHUB_BASE_URL = "https://api.github.com/";
    public static final String MEETUP_BASE_URL = "https://api.meetup.com";
    public static final String MEETUP_API_KEY = BuildConfig.MEETUP_API_KEY;

    // Default search criterias
    public static final int MEETUP_TECH_CATEGORY_NUMBER = 34; // Category "Tech"
    public static final int MEETUP_GROUP_RESPONSE_PAGE = 20;
    public static final String DEFAULT_LOCATION = "tokyo";
    public static final String GITHUB_DEFAULT_SEARCH_KEYWORD = "android";
    public static final String GITHUB_DEFAULT_SORT_ORDER = "updated";

    // Location request related
    public static final int PERMISSIONS_REQUEST_COARSE_LOCATION = 111;
    public static final long LOCATION_UPDATE_INTERVAL = 24 * 60 * 60 * 1000; // 24 hours

    // Keys to open activities
    public static final String MAIN_KEY = "mainKey";
    public static final String MEETUP_MAIN_KEY = "meetupMainKey";
    public static final String GITHUB_MAIN_KEY = "gitHubMainKey";
    public static final String KEY_GROUP_URL = "keyGroupUrl";
    public static final String KEY_EVENT_ID = "keyEventId";

    // Fragment tags
    public static final String TAG_EVENT_DETAILS_FRAGMENT = "meetupEventDetailsFragment";

    // Keys for the dialogs onSavedInstance()
    public static final String KEY_GITHUB_DIALOG_SEARCH_KEYWORD = "gitHubDialogSearchKeyword";
    public static final String KEY_GITHUB_DIALOG_SORT = "gitHubDialogSortKeyword";
    public static final String KEY_GITHUB_DIALOG_ORDER = "gitHubDialogOrderKeyword";
    public static final String KEY_MEETUP_DIALOG_SEARCH_KEYWORD = "meetupDialogSearchKeyword";
    public static final String KEY_MEETUP_DIALOG_SORT = "meetupDialogSortKeyword";
    public static final String KEY_MEETUP_DIALOG_LOCATION = "meetupDialogOrderKeyword";

    // Notifications related
    public static final int UPDATE_REMINDER_NOTIFICATION_ID = 16;
    public static final int UPDATE_REMINDER_PENDING_INTENT_ID = 34;
    public static final String UPDATE_REMINDER_NOTIFICATION_CHANNEL_ID =
            "update_notification_channel";
    public static final int ACTION_OPEN_SEARCH_PENDING_INTENT_ID = 23;
    public static final int ACTION_IGNORE_PENDING_INTENT_ID = 64;

    // JobDispatcher related
    private static final int UPDATE_INTERVAL_HOURS = 12;
    public static final int UPDATE_INTERVAL_SECONDS =
            (int) (TimeUnit.HOURS.toSeconds(UPDATE_INTERVAL_HOURS));
    public static final int SYNC_FLEXTIME_SECONDS = UPDATE_INTERVAL_SECONDS;
    public static final String UPDATE_JOB_TAG = "search_update_tag";
    public static final int ACTION_GET_NOTIFICATION_PENDING_INTENT_ID = 777;

    // Widget related
    public static final int ACTION_ITEM_CLICK_PENDING_INTENT_ID = 888;
    public static final int ACTION_OPEN_DETAILS_PENDING_INTENT_ID = 999;
    public static final String ACTION_OPEN_DETAILS = "com.example.dailyupdate.widget" +
            ".action_open_details";
    public static final String ACTION_UPDATE_WIDGET = "com.example.dailyupdate.widget" +
            ".action_update_widget";
    public static final String ACTION_ITEM_CLICK = "com.example.dailyupdate.widget.action_toast";
    public static final String EXTRA_GROUP_URL = "com.example.dailyupdate.widget" +
            ".extra_group_url";
    public static final String EXTRA_EVENT_ID = "com.example.dailyupdate.widget.extra_event_id";

    // AppService related
    public static final String ACTION_OPEN_SEARCH = "com.example.dailyupdate.utilities" +
            ".action_open_search";
    public static final String ACTION_DISMISS_NOTIFICATION = "com.example.dailyupdate." +
            ".utilities.action_dismiss_notification";
    public static final String ACTION_GET_NOTIFICATION = "com.example.dailyupdate" +
            ".utilities.action_get_notification";
}
