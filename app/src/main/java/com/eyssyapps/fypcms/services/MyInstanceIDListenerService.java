package com.eyssyapps.fypcms.services;

import android.content.Intent;

import com.eyssyapps.fypcms.Protocol;
import com.eyssyapps.fypcms.utils.Constants;
import com.google.android.gms.iid.InstanceIDListenerService;

public class MyInstanceIDListenerService extends InstanceIDListenerService
{
    private static final String TAG = "MyInstanceIDLS";

    @Override
    public void onTokenRefresh()
    {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, RegistrationIntentService.class);
        intent.putExtra(Constants.CLASS_TAG_NAME, MyInstanceIDListenerService.TAG);
        intent.putExtra(Protocol.REREGISTRATION, true);

        startService(intent);
    }
}