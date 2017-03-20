package com.eyssyapps.fypcms.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.eyssyapps.fypcms.Protocol;
import com.eyssyapps.fypcms.R;
import com.eyssyapps.fypcms.activities.student.StudentMainActivity;
import com.eyssyapps.fypcms.activities.student.StudentTimetableActivity;
import com.eyssyapps.fypcms.utils.data.JsonUtils;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Random;

public class MyGcmListenerService extends GcmListenerService
{
    private static final String TAG = "MyGcmListenerService";
    private Random random = new Random(System.currentTimeMillis());
    private static final int TIMETABLE_NOTIFICATION_ID = 100;

    @Override
    public void onMessageReceived(String from, Bundle data)
    {
        Log.d(TAG, "From: " + from);

        if (isHttpMessage(data))
        {
            handleHttpMessage(data);
        }
        else
        {
            handleXmppMessage(data);
        }
    }

    private boolean isHttpMessage(Bundle data)
    {
        return data.containsKey(Protocol.CONTENTS);
    }

    private void handleXmppMessage(Bundle data)
    {
        String action = data.getString(Protocol.ACTION);

        if (action.equals(Protocol.EVENT_CANCELLED))
        {
            prepareEventCancellationFromMobileClient(data);
        }
    }

    private void prepareEventCancellationFromMobileClient(Bundle data)
    {
        int eventId = Integer.valueOf(data.getString(Protocol.VALUE));
        String timestamp = data.getString(Protocol.TIMESTAMP);
        String cancelledBy = data.getString(Protocol.CANCELLED_BY);
        String message = "A class has been cancelled!";

        Intent intent = new Intent(this, StudentTimetableActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Protocol.STANDARD_EVENT_CHANGE, false);
        intent.putExtra(Protocol.CANCELLED_EVENTS, new int[] { eventId });
        intent.putExtra(Protocol.TIMESTAMP, timestamp);
        intent.putExtra(Protocol.CANCELLED_BY, cancelledBy);

        displayNotification(
            "Timetable updated",
            message,
            intent,
            StudentMainActivity.TIMETABLE_START,
            TIMETABLE_NOTIFICATION_ID);
    }

    private void handleHttpMessage(Bundle data)
    {
        String contents = data.getString(Protocol.CONTENTS);

        JsonObject json = JsonUtils.asJsonObject(contents);

        String messageType = json.get(Protocol.MESSAGE_TYPE).getAsString();

        if (messageType.equals(Protocol.NOTIFICATION))
        {
            if (json.has(Protocol.TIMETABLE_CHANGE))
            {
                handleTimetableChanges(json);
            }
            else if (json.has(Protocol.TIMETABLE_CHANGE_CANCELLED_EVENTS))
            {
                handleTimetableEventCancellations(json);
            }
        }
        else
        {
            Intent intent = new Intent(this, StudentMainActivity.class);
            displayNotification("title", "message received", intent, 0, 200);
        }
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void displayNotification(String title, String message, Intent intent, int requestCode, int notificationID)
    {
        PendingIntent pendingIntent =
            PendingIntent.getActivity(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            );

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(Notification.PRIORITY_MAX)
            .setLights(0xff0000ff, 600, 600)
            .setSmallIcon(R.drawable.timetable_white)
            .setSound(defaultSoundUri)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationID, notificationBuilder.build());
    }

    protected void handleTimetableChanges(JsonObject json)
    {
        JsonObject timetableChange = json.get(Protocol.TIMETABLE_CHANGE).getAsJsonObject();
        JsonArray modifiedEvents = timetableChange.get(Protocol.MODIFIED_EVENTS).getAsJsonArray();
        int[] modifiedEventIds = JsonUtils.getIntegerArrayFromJsonArray( modifiedEvents);
        JsonArray newEvents = timetableChange.get(Protocol.NEW_EVENTS).getAsJsonArray();
        int[] newEventIds = JsonUtils.getIntegerArrayFromJsonArray(newEvents);
        JsonArray removedEvents = timetableChange.get(Protocol.REMOVED_EVENTS).getAsJsonArray();
        int[] removedEventIds = JsonUtils.getIntegerArrayFromJsonArray(removedEvents);

        Intent intent = new Intent(this, StudentTimetableActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Protocol.STANDARD_EVENT_CHANGE, true);
        intent.putExtra(Protocol.MODIFIED_EVENTS, modifiedEventIds);
        intent.putExtra(Protocol.NEW_EVENTS, newEventIds);
        intent.putExtra(Protocol.REMOVED_EVENTS, removedEventIds);

        int changeAmount = modifiedEventIds.length + newEventIds.length + removedEventIds.length;
        String message;
        if (changeAmount > 1)
        {
            message = "There are " + changeAmount + " changes made to your timetable!";
        }
        else
        {
            message = "There is a single change to your timetable!";
        }

        displayNotification(
            "Timetable updated",
            message,
            intent,
            StudentMainActivity.TIMETABLE_START,
            TIMETABLE_NOTIFICATION_ID);
    }

    protected void handleTimetableEventCancellations(JsonObject json)
    {
        JsonObject changes = json.get(Protocol.TIMETABLE_CHANGE_CANCELLED_EVENTS).getAsJsonObject();
        JsonArray cancelledEvents = changes.get(Protocol.CANCELLED_EVENTS).getAsJsonArray();
        int[] cancelledEventIds = JsonUtils.getIntegerArrayFromJsonArray(cancelledEvents);
        String message;
        if (cancelledEventIds.length > 1)
        {
            message = "More than one class has been cancelled!";
        }
        else
        {
            message = "A class has been cancelled!";
        }

        Intent intent = new Intent(this, StudentTimetableActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Protocol.CANCELLED_EVENTS, cancelledEventIds);

        displayNotification(
            "Timetable changes",
            message,
            intent,
            StudentMainActivity.TIMETABLE_START,
            TIMETABLE_NOTIFICATION_ID);
    }
}