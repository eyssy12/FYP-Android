package com.eyssyapps.fypcms.activities.lecturer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
import com.eyssyapps.fypcms.utils.view.SystemMessagingUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LecturerTimetableActivity extends AppCompatActivity
{
    private Retrofit retrofit;
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
            Call<IdTokenResponse> call = authService.refreshIdtoken(new RefreshTokenRequest(
                    accessToken));

            call.enqueue(new Callback<IdTokenResponse>()
            {
                @Override
                public void onResponse(Call<IdTokenResponse> call, Response<IdTokenResponse> response)
                {
                    RetrofitProviderService.replaceIdTokenWithLatest(sharedPreferences,
                            response.body());

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
                // how to handle timeout ?
                progressDialog.dismiss();
            }
        });
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
}
