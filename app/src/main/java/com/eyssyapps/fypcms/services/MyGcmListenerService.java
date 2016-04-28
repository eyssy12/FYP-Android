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

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data)
    {
        Log.d(TAG, "From: " + from);

        String contents = data.getString(Protocol.CONTENTS);

        if (contents == null || contents.isEmpty())
        {
            return;
        }

        JsonObject json = JsonUtils.asJsonObject(contents);

        String messageType = json.get(Protocol.MESSAGE_TYPE).getAsString();

        if (messageType.equals(Protocol.NOTIFICATION))
        {
            JsonObject timetableChange = json.get(Protocol.TIMETABLE_CHANGE).getAsJsonObject();

            if (timetableChange != null)
            {
                JsonArray modifiedEvents = timetableChange.get(Protocol.MODIFIED_EVENTS).getAsJsonArray();
                int[] modifiedEventIds = JsonUtils.getIntegerArrayFromJsonArray(
                        modifiedEvents);

                JsonArray newEvents = timetableChange.get(Protocol.NEW_EVENTS).getAsJsonArray();
                int[] newEventIds = JsonUtils.getIntegerArrayFromJsonArray(newEvents);

                JsonArray removedEvents = timetableChange.get(Protocol.REMOVED_EVENTS).getAsJsonArray();
                int[] removedEventIds = JsonUtils.getIntegerArrayFromJsonArray(removedEvents);

                Intent intent = new Intent(this, StudentTimetableActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(Protocol.MODIFIED_EVENTS, modifiedEventIds);
                intent.putExtra(Protocol.MODIFIED_EVENTS, newEventIds);
                intent.putExtra(Protocol.MODIFIED_EVENTS, removedEventIds);

                int changeAmount = modifiedEventIds.length + newEventIds.length + removedEventIds.length;

                String message;

                if (changeAmount > 1)
                {
                    message = "There are " + changeAmount + " changes made to your timetable.";
                }
                else
                {
                    message = "There is a single change to your timetable";
                }

                displayNotification(
                    "Timetable updated!",
                    message,
                    intent,
                    StudentMainActivity.TIMETABLE_START,
                    TIMETABLE_NOTIFICATION_ID);
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
            .setSmallIcon(R.drawable.timetable)
            .setSound(defaultSoundUri)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationID, notificationBuilder.build());
    }
}