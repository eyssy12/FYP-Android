package com.eyssyapps.fypcms.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eyssyapps.fypcms.Protocol;
import com.eyssyapps.fypcms.R;
import com.eyssyapps.fypcms.managers.PreferencesManager;
import com.eyssyapps.fypcms.services.RegistrationIntentService;
import com.eyssyapps.fypcms.utils.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.Random;

public class SendMessageActivity extends AppCompatActivity
{
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private final String TAG = "SendMessageActivity",
        ANDROID_API = "AIzaSyD4n3Ptn8mkItmnpwXZ1FV0HKPPROfi93k",
        SERVER_API = "AIzaSyDF58nZS8k8KzA5lo4l2S4lViYFBhRq7jA";

    private Button showTokenBtn,
        showInstanceIdBtn,
        sendBtn;

    private EditText messageEditTxt;

    private Context mContext;
    private PreferencesManager sharedPrefs;
    private GoogleCloudMessaging gcm;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.mContext = getBaseContext();
        this.sharedPrefs = PreferencesManager.getInstance(this);
        this.gcm = GoogleCloudMessaging.getInstance(SendMessageActivity.this);
        this.random = new Random();

        this.showTokenBtn = (Button)findViewById(R.id.showTokenBtn);
        this.showTokenBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String test = sharedPrefs.getString(Constants.GCM_TOKEN, "default string");
                Toast.makeText(mContext, test, Toast.LENGTH_SHORT).show();
            }
        });

        this.showInstanceIdBtn = (Button)findViewById(R.id.showInstanceIdBtn);
        this.showInstanceIdBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String test = sharedPrefs.getString(Constants.INSTANCE_ID, "default string");
                Toast.makeText(mContext, test, Toast.LENGTH_SHORT).show();
            }
        });

        this.messageEditTxt = (EditText) findViewById(R.id.messageEditTxt);

        this.sendBtn = (Button) findViewById(R.id.sendBtn);
        this.sendBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                new AsyncTask<String, Void, String>()
                {
                    @Override
                    protected String doInBackground(String... params)
                    {
                        String message = params[0];
                        String status;
                        try
                        {
                            Bundle data = new Bundle();
                            data.putString(Protocol.ACTION, Protocol.MESSAGE);
                            data.putString(Protocol.VALUE, message);

                            int msgId = random.nextInt(10000000) + 1;

                            String id = Integer.toString(msgId);

                            gcm.send(Protocol.CCS_SERVER_ENDPOINT, id, data);

                            status = "Sent message";
                        }
                        catch (IOException ex)
                        {
                            status = "Error :" + ex.getMessage();
                        }

                        return status;
                    }

                    @Override
                    protected void onPostExecute(String status)
                    {
                        Snackbar.make(v, status, Snackbar.LENGTH_SHORT)
                                .show();
                    }
                }.execute(messageEditTxt.getText()
                        .toString());
            }
        });

        if (checkPlayServices())
        {
            // Start IntentService to register this application with GCM.
            startDeviceRegistrationService(false);
        }
    }

    @Override
    public void onBackPressed()
    {
        this.finishActivity();
    }

    private void finishActivity()
    {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.reset_sharedpreferences)
        {
            clearSharedPreferences();

            return true;
        }
        else if (id == R.id.register_to_gcm)
        {
            startDeviceRegistrationService(true);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startDeviceRegistrationService(boolean isRegistrating)
    {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        intent.putExtra(Protocol.REREGISTRATION, isRegistrating);

        startService(intent);
    }

    private void clearSharedPreferences()
    {
        try
        {
            sharedPrefs.clear();

            Toast.makeText(SendMessageActivity.this, "Shared preferences cleared!", Toast.LENGTH_SHORT).show();
        }
        catch(Exception ex)
        {
            Toast.makeText(SendMessageActivity.this, "Couldnt clear shared preferences", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPlayServices()
    {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (apiAvailability.isUserResolvableError(resultCode))
            {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Log.i(TAG, "This device is not supported.");
                finish();
            }

            return false;
        }

        return true;
    }
}
