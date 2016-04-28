package com.eyssyapps.fypcms;

/**
 * Created by eyssy on 09/02/2016.
 */
public class Protocol
{
    public static final String
        MESSAGE_TYPE = "message_type",
        NOTIFICATION = "notification",
        AUTHENTICATED = "authenticated",
        CONTENTS = "contents",
        STUDENT = "student",
        LECTURER = "lecturer",
        ACTION = "action",
        VALUE = "value",
        ENTITY_ID = "entity_id",
        REGISTRATION = "registration",
        REREGISTRATION = "reregistration",
        MESSAGE = "message",
        TIMETABLE_CHANGE = "timetable_change",
        TIMETABLE_CHANGE_CANCELLED_EVENTS = "timetable_change_cancelled_events",
        CCS_SERVER_ENDPOINT_ADDRESS = "@gcm.googleapis.com",
        SENDER_ID = "334770348820",
        PROJECT_ID = "fyp-cms-college",
        ENDPOINT_PRE_PROD = "gcm-preprod.googleapis.com",
        ENDPINT_PRODUCTION = "gcm-xmpp.googleapis.com",
        CCS_SERVER_ENDPOINT = Protocol.SENDER_ID + Protocol.CCS_SERVER_ENDPOINT_ADDRESS,
        MODIFIED_EVENTS = "modified_events",
        NEW_EVENTS = "new_events",
        REMOVED_EVENTS = "removed_events",
        CANCELLED_EVENTS = "cancelled_event_ids",
        STANDARD_EVENT_CHANGE = "standard_event_change";
}
