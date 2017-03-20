package com.eyssyapps.fypcms.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.eyssyapps.fypcms.Protocol;
import com.eyssyapps.fypcms.R;
import com.eyssyapps.fypcms.managers.PreferencesManager;
import com.eyssyapps.fypcms.utils.Constants;
import com.eyssyapps.fypcms.utils.networking.GcmUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.util.Random;

public class RegistrationIntentService extends IntentService
{
    private static final String TAG = "RegIntentService";

    private GoogleCloudMessaging gcm;
    private PreferencesManager sharedPreferences;
    private Random random;

    public RegistrationIntentService()
    {
        super(TAG);
        this.gcm = GoogleCloudMessaging.getInstance(this);
        this.sharedPreferences = PreferencesManager.getInstance(this);
        this.random = new Random();
    }

    public static boolean checkRegistrationStatus(Context context, PreferencesManager sharedPreferences)
    {
        boolean isTokenSentAlready = sharedPreferences.getBoolean(Constants.SENT_TOKEN_TO_SERVER, false);
        boolean shouldReRegister = sharedPreferences.getBoolean(PreferencesManager.PREFS_DATA_DO_GCM_REREGISTRATION, false);

        if (shouldReRegister || !isTokenSentAlready)
        {
            if (!isTokenSentAlready)
            {
                // Start IntentService to register this application with GCM.
                startDeviceRegistrationService(context, false);
            }
            else if (shouldReRegister)
            {
                startDeviceRegistrationService(context, true);
            }
        }

        return true;
    }

    private static void startDeviceRegistrationService(Context context, boolean isReRegistration)
    {
        Intent intent = new Intent(context, RegistrationIntentService.class);
        intent.putExtra(Protocol.REREGISTRATION, isReRegistration);

        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        try
        {
            Bundle bundle = intent.getExtras();
            boolean isReRegistration = bundle.getBoolean(Protocol.REREGISTRATION);
            boolean isTokenSentToServer = sharedPreferences.getBoolean(Constants.SENT_TOKEN_TO_SERVER, false);

            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.i(Constants.CLASS_TAG_NAME, "GCM Registration TokenData: " + token);

            checkRegistrationRequirements(token, isReRegistration, isTokenSentToServer);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.

            sharedPreferences.putBoolean(Constants.SENT_TOKEN_TO_SERVER, true);
            sharedPreferences.putString(Constants.GCM_TOKEN, token);
            sharedPreferences.putString(Constants.INSTANCE_ID, instanceID.getId());

            // Notify UI that registration has completed, so the progress indicator can be hidden.
            Intent registrationComplete = new Intent(Constants.REGISTRATION_COMPLETE);
            LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
        } catch (Exception e)
        {
            Log.d(Constants.CLASS_TAG_NAME, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.putBoolean(Constants.SENT_TOKEN_TO_SERVER, false);
        }
    }

    private void checkRegistrationRequirements(String token, boolean isReRegistration, boolean isTokenSentToServer)
    {
        // we don't we to resend a token if we've already done it beforehand
        // we want to send it again in the case of a new token being registered (can happen for an android device) which is why this is a service in the first place
        // or we want to reregister if a new user has logged in the device - device always has the same unique id - the backend will figure out then who to send notifications to later on
        if (isReRegistration || !isTokenSentToServer)
        {
            String action;
            if (!isTokenSentToServer)
            {
                action = Protocol.REGISTRATION;
            }
            else
            {
                action = Protocol.REREGISTRATION;
            }

            String entityId = sharedPreferences.getStringWithDefault(PreferencesManager.PREFS_DATA_ENTITY_ID);
            sendRegistrationToServer(token, action, entityId);
            sharedPreferences.putBoolean(PreferencesManager.PREFS_DATA_DO_GCM_REREGISTRATION, false);
        }
    }

    private void sendRegistrationToServer(String token, String action, String entityId)
    {
        // Add custom implementation, as needed.
        Bundle data = new Bundle();
        data.putString(Protocol.ACTION, action);
        data.putString(Protocol.VALUE, token);
        data.putString(Protocol.ENTITY_ID, entityId);

        GcmUtils.sendMessage(gcm, data);
    }
}