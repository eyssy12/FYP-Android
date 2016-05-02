package com.eyssyapps.fypcms.utils.networking;

import android.os.Bundle;

import com.eyssyapps.fypcms.Protocol;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.Random;

/**
 * Created by eyssy on 02/05/2016.
 */
public class GcmUtils
{
    private static final Random RANDOM = new Random();

    public static void sendMessage(GoogleCloudMessaging gcm, Bundle data)
    {
        int msgId = RANDOM.nextInt(10000000) + 1;

        try
        {
            gcm.send(Protocol.CCS_SERVER_ENDPOINT, Integer.toString(msgId), data);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
