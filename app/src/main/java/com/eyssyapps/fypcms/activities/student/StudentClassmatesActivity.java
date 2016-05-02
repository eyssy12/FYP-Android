package com.eyssyapps.fypcms.activities.student;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.eyssyapps.fypcms.R;
import com.eyssyapps.fypcms.adapters.ClassmatesRecyclerViewAdapter;
import com.eyssyapps.fypcms.custom.EmptyRecyclerView;
import com.eyssyapps.fypcms.services.retrofit.AuthService;
import com.eyssyapps.fypcms.services.retrofit.ClassService;
import com.eyssyapps.fypcms.managers.PreferencesManager;
import com.eyssyapps.fypcms.models.IdTokenResponse;
import com.eyssyapps.fypcms.models.RefreshTokenRequest;
import com.eyssyapps.fypcms.models.StudentPerson;
import com.eyssyapps.fypcms.models.WebApiArguments;
import com.eyssyapps.fypcms.services.RetrofitProviderService;
import com.eyssyapps.fypcms.utils.view.SystemMessagingUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class StudentClassmatesActivity extends AppCompatActivity
{
    private EmptyRecyclerView emptyRecyclerView;
    private LinearLayoutManager layoutManager;

    private Retrofit retrofit;
    private PreferencesManager sharedPreferences;
    private ClassmatesRecyclerViewAdapter classmatesAdapter;
    private ClassService classService;
    private AuthService authService;

    @Bind (R.id.content_student_classmates)
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_classmates);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // setting refreshLayout directly does not work, this is a workaround
        swipeRefreshLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        retrofit = RetrofitProviderService.getDefaultInstance();
        sharedPreferences = PreferencesManager.getInstance(StudentClassmatesActivity.this);
        classService = retrofit.create(ClassService.class);
        authService = retrofit.create(AuthService.class);

        layoutManager = new LinearLayoutManager(StudentClassmatesActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        emptyRecyclerView = (EmptyRecyclerView)swipeRefreshLayout.findViewById(R.id.recycler_view_empty_support);
        emptyRecyclerView.setLayoutManager(new LinearLayoutManager(StudentClassmatesActivity.this));
        classmatesAdapter = new ClassmatesRecyclerViewAdapter(StudentClassmatesActivity.this, emptyRecyclerView);
        emptyRecyclerView.setEmptyView(swipeRefreshLayout.findViewById(R.id.empty_recycler_view_state_layout));
        emptyRecyclerView.setAdapter(classmatesAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                swipeRefreshLayout.setRefreshing(true);
                fetchClassmates();
            }
        });

        this.checkToken();
    }

    private void checkToken()
    {
        if (RetrofitProviderService.isTokenExpired(sharedPreferences))
        {
            String accessToken = sharedPreferences.getStringWithDefault(PreferencesManager.PREFS_DATA_ACCESS_TOKEN);
            Call<IdTokenResponse> call = authService.refreshIdtoken(new RefreshTokenRequest(accessToken));

            call.enqueue(new Callback<IdTokenResponse>() {
                @Override
                public void onResponse(Call<IdTokenResponse> call, Response<IdTokenResponse> response)
                {
                    RetrofitProviderService.replaceIdTokenWithLatest(sharedPreferences, response.body());

                    fetchClassmates();
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
            fetchClassmates();
        }
    }

    private void fetchClassmates()
    {
        WebApiArguments arguments = RetrofitProviderService.getStandardWebApiArguments(sharedPreferences);

        Call<List<StudentPerson>> call = classService.getClassmatesForStudent(arguments.getUserId(), arguments.getAuthorizedBearer());
        call.enqueue(new Callback<List<StudentPerson>>() {
            @Override
            public void onResponse(Call<List<StudentPerson>> call, Response<List<StudentPerson>> response)
            {
                if (RetrofitProviderService.checkOk(response))
                {
                    List<StudentPerson> classmates = response.body();

                    classmatesAdapter.replaceCollection(classmates, true);

                    swipeRefreshLayout.setRefreshing(false);

                    SystemMessagingUtils.createSnackbar(swipeRefreshLayout,
                        "All classmates retrieved",
                        Snackbar.LENGTH_SHORT).show();
                }
                else if (RetrofitProviderService.checkNewTokenRequired(response))
                {
                    String accessToken = sharedPreferences.getStringWithDefault(PreferencesManager.PREFS_DATA_ACCESS_TOKEN);
                    // need to get a new token
                    Call<IdTokenResponse> refreshCall = authService.refreshIdtoken(new RefreshTokenRequest(accessToken));
                    refreshCall.enqueue(new Callback<IdTokenResponse>() {
                        @Override
                        public void onResponse(Call<IdTokenResponse> call, Response<IdTokenResponse> response)
                        {
                            IdTokenResponse refreshResponse = response.body();

                            sharedPreferences.putString(PreferencesManager.PREFS_DATA_ID_TOKEN, refreshResponse.getIdToken());
                            sharedPreferences.putString(PreferencesManager.PREFS_DATA_ID_TOKEN_EXPIRY, refreshResponse.getIdTokenExpiry());

                            fetchClassmates();
                        }

                        @Override
                        public void onFailure(Call<IdTokenResponse> call, Throwable t)
                        {
                            SystemMessagingUtils.showToast(
                                StudentClassmatesActivity.this,
                                "There is a problem with the service and requires you to reauthenticate yourself.",
                                Toast.LENGTH_SHORT);

                            setResult(RESULT_CANCELED);
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<StudentPerson>> call, Throwable t)
            {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finishActivity();
                break;
        }
        return true;
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