package com.eyssyapps.fypcms.activities.lecturer;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.eyssyapps.fypcms.Protocol;
import com.eyssyapps.fypcms.R;
import com.eyssyapps.fypcms.custom.TabbedWeekViewPager;
import com.eyssyapps.fypcms.enumerations.TimetableType;
import com.eyssyapps.fypcms.managers.PreferencesManager;
import com.eyssyapps.fypcms.models.Event;
import com.eyssyapps.fypcms.models.IdTokenResponse;
import com.eyssyapps.fypcms.models.LecturerTimetable;
import com.eyssyapps.fypcms.models.RefreshTokenRequest;
import com.eyssyapps.fypcms.models.WebApiArguments;
import com.eyssyapps.fypcms.services.RetrofitProviderService;
import com.eyssyapps.fypcms.services.retrofit.AuthService;
import com.eyssyapps.fypcms.services.retrofit.TimetableService;
import com.eyssyapps.fypcms.utils.Constants;
import com.eyssyapps.fypcms.utils.networking.GcmUtils;
import com.eyssyapps.fypcms.utils.view.SystemMessagingUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LecturerTimetableActivity extends AppCompatActivity
{
    private BasicReceiver cancellationReceiver;
    private IntentFilter intentFilter;

    private Retrofit retrofit;
    private GoogleCloudMessaging gcm;
    private PreferencesManager sharedPreferences;
    private AuthService authService;
    private TimetableService timetableService;
    private ProgressDialog progressDialog;

    private TabbedWeekViewPager tabbedWeekViewPager;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.lecturer_timetable_main_content)
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_timetable);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cancellationReceiver = new BasicReceiver();
        intentFilter = new IntentFilter(Constants.CLASS_CANCELLED);
        registerReceiver(cancellationReceiver, intentFilter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Preparing timetable...");
        progressDialog.show();

        Intent passedInArgs = getIntent();

        if (passedInArgs != null)
        {
            // this means we're arguments from the GcmListenerService
        }

        gcm = GoogleCloudMessaging.getInstance(this);
        sharedPreferences = PreferencesManager.getInstance(this);
        retrofit = RetrofitProviderService.getDefaultInstance();
        authService = retrofit.create(AuthService.class);
        timetableService = retrofit.create(TimetableService.class);

        tabbedWeekViewPager = new TabbedWeekViewPager(this, coordinatorLayout, TimetableType.LECTURER);
        this.checkToken();
    }

    private void checkToken()
    {
        if (RetrofitProviderService.isTokenExpired(sharedPreferences))
        {
            String accessToken = sharedPreferences.getStringWithDefault(PreferencesManager.PREFS_DATA_ACCESS_TOKEN);

            Call<IdTokenResponse> call = authService.refreshIdtoken(new RefreshTokenRequest(accessToken));
            call.enqueue(new Callback<IdTokenResponse>()
            {
                @Override
                public void onResponse(Call<IdTokenResponse> call, Response<IdTokenResponse> response)
                {
                    RetrofitProviderService.replaceIdTokenWithLatest(sharedPreferences,response.body());

                    fetchTimetable();
                }

                @Override
                public void onFailure(Call<IdTokenResponse> call, Throwable t)
                {
                    setResult(RESULT_CANCELED);

                    finish();
                }
            });
        }
        else
        {
            fetchTimetable();
        }
    }

    private void fetchTimetable()
    {
        WebApiArguments arguments = RetrofitProviderService.getStandardWebApiArguments(sharedPreferences);

        Call<List<Event>> call = timetableService.getTimetableForLecturer(arguments.getUserId(), arguments.getAuthorizedBearer());
        call.enqueue(new Callback<List<Event>>()
        {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response)
            {
                if (RetrofitProviderService.checkOk(response))
                {
                    LecturerTimetable timetable = new LecturerTimetable(response.body());

                    if (timetable.getEvents().isEmpty())
                    {
                        SystemMessagingUtils.createSnackbar(tabbedWeekViewPager.getViewPager(), "Timetable retrieved - no classes have been configured", Snackbar.LENGTH_SHORT).show();
                    }
                    else
                    {
                        tabbedWeekViewPager.getMondayAdapter().replaceCollection(timetable.getMondayEvents(), true);
                        tabbedWeekViewPager.getTuesdayAdapter().replaceCollection(timetable.getTuesdayEvents(), true);
                        tabbedWeekViewPager.getWednesdayAdapter().replaceCollection(timetable.getWednesdayEvents(), true);
                        tabbedWeekViewPager.getThursdayAdapter().replaceCollection(timetable.getThursdayEvents(), true);
                        tabbedWeekViewPager.getFridayAdapter().replaceCollection(timetable.getFridayEvents(), true);

                        SystemMessagingUtils.createSnackbar(tabbedWeekViewPager.getViewPager(), "Timetable retrieved", Snackbar.LENGTH_SHORT).show();
                    }

                    progressDialog.dismiss();
                }
                else if (RetrofitProviderService.checkNewTokenRequired(response))
                {
                    String accessToken = sharedPreferences.getStringWithDefault(PreferencesManager.PREFS_DATA_ACCESS_TOKEN);
                    // need to get a new token
                    Call<IdTokenResponse> refreshCall = authService.refreshIdtoken(new RefreshTokenRequest(accessToken));
                    refreshCall.enqueue(new Callback<IdTokenResponse>()
                    {
                        @Override
                        public void onResponse(Call<IdTokenResponse> call, Response<IdTokenResponse> response)
                        {
                            RetrofitProviderService.replaceIdTokenWithLatest(sharedPreferences, response.body());

                            fetchTimetable();
                        }

                        @Override
                        public void onFailure(Call<IdTokenResponse> call, Throwable t)
                        {
                            SystemMessagingUtils.createToast(
                                LecturerTimetableActivity.this,
                                "There is a problem with the service and requires you to reauthenticate yourself.",
                                Toast.LENGTH_SHORT).show();

                            setResult(RESULT_CANCELED);
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t)
            {
                if (t instanceof SocketTimeoutException)
                {
                    progressDialog.setMessage("API is asleep, retrying...");

                    fetchTimetable();
                }
                else
                {
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        // LocalBroadcastManager.getInstance(this) doesnt work
        unregisterReceiver(cancellationReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_lecturer_timetable, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finishActivity();
                break;
            case R.id.action_refresh:
                progressDialog.show();
                fetchTimetable();
                break;
        }

        return super.onOptionsItemSelected(item);
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

    private class BasicReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context arg0, Intent arg1)
        {
            String action = arg1.getAction();

            if (action.equals(Constants.CLASS_CANCELLED))
            {
                Bundle bundle = arg1.getExtras();

                int eventId = bundle.getInt(Protocol.TIMETABLE_CHANGE_CANCELLED_EVENT_ID);
                String entityId = sharedPreferences.getStringWithDefault(PreferencesManager.PREFS_DATA_ENTITY_ID);
                Date now = new Date();

                Bundle data = new Bundle();
                data.putString(Protocol.ACTION, Protocol.EVENT_CANCELLED);
                data.putString(Protocol.VALUE, String.valueOf(eventId));
                data.putString(Protocol.ENTITY_ID, entityId);
                data.putString(Protocol.TIMESTAMP, String.valueOf(now.getTime()));

                GcmUtils.sendMessage(gcm, data);
            }
        }
    }
}