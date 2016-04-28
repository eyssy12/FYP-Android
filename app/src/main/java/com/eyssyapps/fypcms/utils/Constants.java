package com.eyssyapps.fypcms.utils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by eyssy on 19/02/2016.
 */
public class Constants
{
    public static final Map<Integer, String> ENROLLMENT_STAGES = new HashMap<>();
    public static final Map<Integer, String> DEGREE_AWARDS = new HashMap<>();
    public static final Map<Integer, String> MODULE_TYPES = new HashMap<>();
    public static final Map<Integer, String> DAYS_OF_WEEK = new HashMap<>();

    public static final TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone("UTC");
    public static final SimpleDateFormat DEFAULT_SIMPLE_DATE_FORMAT,
        DEFAULT_EVENT_DISPLAY_SIMPLE_DATE_FORMAT,
        DEFAULT_NEWS_POST_SIMPLE_DATE_FORMAT;

    public static final String SECURITY_LOGOUT = "security_logout",
            SENT_TOKEN_TO_SERVER = "sent_token_to_server",
            REGISTRATION_COMPLETE = "registration_complete",
            INSTANCE_ID = "instance_id",
            GCM_TOKEN = "gcm_token",
            SHARD_PREFERENCES_NAME = "com.eyssyapps.fypcms.gcmclient_preferences",
            CLASS_TAG_NAME = "tag",
            API_BASE = "https://fypcmsapi.azurewebsites.net/api/",
            DATETIME_FORMAT = "yyyy-MM-dd'T'hh:mm:ss",
            NEWS_POST_DATETIME_FORMAT = "MMM d, yyyy 'at' h:mm a",
            EVENT_DISPLAY_DATETIME_FORMAT = "hh:mm a";

    static
    {
        ENROLLMENT_STAGES.put(1, "First");
        ENROLLMENT_STAGES.put(2, "Second");
        ENROLLMENT_STAGES.put(3, "Third");
        ENROLLMENT_STAGES.put(4, "Fourth");

        DEGREE_AWARDS.put(1, "Higher Diploma");
        DEGREE_AWARDS.put(2, "Bachelors Ordinary");
        DEGREE_AWARDS.put(3, "Bachelors Higher");
        DEGREE_AWARDS.put(4, "Masters");
        DEGREE_AWARDS.put(5, "PhD");

        MODULE_TYPES.put(1, "Mandatory");
        MODULE_TYPES.put(2, "Elective");

        DAYS_OF_WEEK.put(1, "Mon");
        DAYS_OF_WEEK.put(2, "Tue");
        DAYS_OF_WEEK.put(3, "Wed");
        DAYS_OF_WEEK.put(4, "Thu");
        DAYS_OF_WEEK.put(5, "Fri");

        DEFAULT_SIMPLE_DATE_FORMAT = new SimpleDateFormat(Constants.DATETIME_FORMAT);
        DEFAULT_EVENT_DISPLAY_SIMPLE_DATE_FORMAT = new SimpleDateFormat(Constants.EVENT_DISPLAY_DATETIME_FORMAT);
        DEFAULT_NEWS_POST_SIMPLE_DATE_FORMAT = new SimpleDateFormat(Constants.NEWS_POST_DATETIME_FORMAT);
    }
}
