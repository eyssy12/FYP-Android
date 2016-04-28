package com.eyssyapps.fypcms.services;

import android.content.Intent;

import com.eyssyapps.fypcms.Protocol;
import com.eyssyapps.fypcms.utils.Constants;
import com.google.android.gms.iid.InstanceIDListenerService;

public class MyInstanceIDListenerService extends InstanceIDListenerService
{
    private static final String TAG = "MyInstanceIDLS";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
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