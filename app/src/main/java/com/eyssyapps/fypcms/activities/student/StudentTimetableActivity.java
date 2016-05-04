package com.eyssyapps.fypcms.activities.student;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import com.eyssyapps.fypcms.models.CancelledEvent;
import com.eyssyapps.fypcms.models.IdTokenResponse;
import com.eyssyapps.fypcms.models.RefreshTokenRequest;
import com.eyssyapps.fypcms.models.StudentTimetable;
import com.eyssyapps.fypcms.models.Timetable;
import com.eyssyapps.fypcms.models.WebApiArguments;
import com.eyssyapps.fypcms.services.RetrofitProviderService;
import com.eyssyapps.fypcms.services.retrofit.AuthService;
import com.eyssyapps.fypcms.services.retrofit.TimetableService;
import com.eyssyapps.fypcms.utils.Constants;
import com.eyssyapps.fypcms.utils.view.SystemMessagingUtils;

import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class StudentTimetableActivity extends AppCompatActivity
{
    private Retrofit retrofit;
    private PreferencesManager sharedPreferences;
    private AuthService authService;
    private TimetableService timetableService;
    private ProgressDialog progressDialog;

    private Timetable timetable;
    private TabbedWeekViewPager tabbedWeekViewPager;
    private int[] newEventIds,
        modifiedEventIds,
        removedEventIds;

    private List<CancelledEvent> cancelledEvents;

    private boolean initialLoadFinished = false, timetableLoaded = false, cancelledEventsLoaded = false;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.student_timetable_main_content)
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_timetable);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Preparing timetable...");
        progressDialog.show();

        // this means we're arguments from the GcmListenerService
        Bundle extras = getIntent().getExtras();
        cancelledEvents = new ArrayList<>();

        if (extras != null)
        {
            if (extras.containsKey(Protocol.CANCELLED_EVENTS))
            {
                int[] cancelledEventsIds = extras.getIntArray(Protocol.CANCELLED_EVENTS);
                String timestamp = extras.getString(Protocol.TIMESTAMP);
                String cancelledBy = extras.getString(Protocol.CANCELLED_BY);

                for (int id : cancelledEventsIds)
                {
                    cancelledEvents.add(new CancelledEvent(id, timestamp, cancelledBy, ""));
                }
            }
            else if (extras.containsKey(Protocol.STANDARD_EVENT_CHANGE))
            {
                newEventIds = extras.getIntArray(Protocol.NEW_EVENTS);
                modifiedEventIds = extras.getIntArray(Protocol.MODIFIED_EVENTS);
                removedEventIds = extras.getIntArray(Protocol.REMOVED_EVENTS);
            }
        }

        sharedPreferences = PreferencesManager.getInstance(this);
        retrofit = RetrofitProviderService.getDefaultInstance();
        authService = retrofit.create(AuthService.class);
        timetableService = retrofit.create(TimetableService.class);

        tabbedWeekViewPager = new TabbedWeekViewPager(this, coordinatorLayout, TimetableType.STUDENT);
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
                    RetrofitProviderService.replaceIdTokenWithLatest(sharedPreferences, response.body());

                    fetchData();
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
            fetchData();
        }
    }

    private void fetchData()
    {
        fetchTimetable();
        fetchCancelledEvents();
    }

    private void fetchCancelledEvents()
    {
        WebApiArguments arguments = RetrofitProviderService.getStandardWebApiArguments(sharedPreferences);

        Call<List<CancelledEvent>> call = timetableService.getCancelledEventsForStudent(arguments.getUserId(), arguments.getAuthorizedBearer());
        call.enqueue(new Callback<List<CancelledEvent>>()
        {
            @Override
            public void onResponse(Call<List<CancelledEvent>> call, Response<List<CancelledEvent>> response)
            {
                if (RetrofitProviderService.checkOk(response))
                {
                    cancelledEvents = response.body();

                    reportCancelledEventsLoaded();
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

                            fetchCancelledEvents();
                        }

                        @Override
                        public void onFailure(Call<IdTokenResponse> call, Throwable t)
                        {
                            SystemMessagingUtils.showToast(
                                StudentTimetableActivity.this,
                                "There is a problem with the service and requires you to re-authenticate yourself.",
                                Toast.LENGTH_SHORT);

                            setResult(RESULT_CANCELED);
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<CancelledEvent>> call, Throwable t)
            {
                if (t instanceof SocketTimeoutException)
                {
                    progressDialog.setMessage("API is asleep, retrying...");

                    fetchCancelledEvents();
                }
            }
        });
    }

    private void fetchTimetable()
    {
        WebApiArguments arguments = RetrofitProviderService.getStandardWebApiArguments(sharedPreferences);

        Call<StudentTimetable> call = timetableService.getTimetableForStudent(arguments.getUserId(), arguments.getAuthorizedBearer());
        call.enqueue(new Callback<StudentTimetable>()
        {
            @Override
            public void onResponse(Call<StudentTimetable> call, Response<StudentTimetable> response)
            {
                if (RetrofitProviderService.checkOk(response))
                {
                    timetable = response.body();

                    if (timetable != null && !timetable.getEvents().isEmpty())
                    {
                        tabbedWeekViewPager.getMondayAdapter().replaceCollection(timetable.getMondayEvents(), true);
                        tabbedWeekViewPager.getTuesdayAdapter().replaceCollection(timetable.getTuesdayEvents(), true);
                        tabbedWeekViewPager.getWednesdayAdapter().replaceCollection(timetable.getWednesdayEvents(), true);
                        tabbedWeekViewPager.getThursdayAdapter().replaceCollection(timetable.getThursdayEvents(), true);
                        tabbedWeekViewPager.getFridayAdapter().replaceCollection(timetable.getFridayEvents(), true);

                        SystemMessagingUtils.createSnackbar(tabbedWeekViewPager.getViewPager(), "Timetable retrieved", Snackbar.LENGTH_SHORT).show();
                    }

                    reportTimetableLoaded();
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
                            SystemMessagingUtils.showToast(
                                StudentTimetableActivity.this,
                                "There is a problem with the service and requires you to reauthenticate yourself.",
                                Toast.LENGTH_SHORT);

                            setResult(RESULT_CANCELED);
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<StudentTimetable> call, Throwable t)
            {
                if (t instanceof SocketTimeoutException)
                {
                    progressDialog.setMessage("API is asleep, retrying...");

                    fetchTimetable();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_student_timetable, menu);
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
            case R.id.action_cancellations:
                showCancellations();
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

    private void showCancellations()
    {
        if (cancelledEvents == null || cancelledEvents.size() < 1)
        {
            SystemMessagingUtils.showToast(this, "No cancellations to show", Toast.LENGTH_SHORT);
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                .setTitle("Cancelled classes")
                .setIcon(R.drawable.timetable_event_cancelled)
                .setCancelable(true)
                .setItems(prepareCancelledEventsForAlertDialog(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                })
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
        }
    }

    private String[] prepareCancelledEventsForAlertDialog()
    {
        String[] items = new String[cancelledEvents.size()];

        int index = 0;
        for (CancelledEvent cancelledEvent : cancelledEvents)
        {
            try
            {
                Date date = Constants.DEFAULT_SIMPLE_DATE_FORMAT.parse(cancelledEvent.getTimestamp());
                String cancellationTimestamp = Constants.DEFAULT_NEWS_POST_SIMPLE_DATE_FORMAT.format(date);

                String formatted =
                    "\n-> " +
                    cancelledEvent.getCancelledEventTitle() + " " +
                    cancelledEvent.getFormattedStartAndEndDate() +
                    "\nBy: " + cancelledEvent.getCancelledBy() +
                    "\nOn: " + cancellationTimestamp;

                items[index] = formatted;

                index++;
            }
            catch (ParseException e)
            {
                e.printStackTrace();
                break;
            }
        }

        return items;
    }

    private void reportTimetableLoaded()
    {
        timetableLoaded = true;

        checkLoadingStatus();
    }

    private void reportCancelledEventsLoaded()
    {
        cancelledEventsLoaded = true;

        checkLoadingStatus();
    }

    private void checkLoadingStatus()
    {
        if (!initialLoadFinished && (timetableLoaded && cancelledEventsLoaded))
        {
            progressDialog.dismiss();

            SystemMessagingUtils.showSnackBar(
                coordinatorLayout,
                "Timetable loaded",
                Toast.LENGTH_SHORT);

            initialLoadFinished = true;
        }
    }
}
