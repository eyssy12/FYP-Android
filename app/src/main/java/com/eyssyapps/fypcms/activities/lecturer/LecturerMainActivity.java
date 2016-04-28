package com.eyssyapps.fypcms.activities.lecturer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eyssyapps.fypcms.R;
import com.eyssyapps.fypcms.activities.LoginActivity;
import com.eyssyapps.fypcms.adapters.NewsRecyclerViewAdapter;
import com.eyssyapps.fypcms.custom.EmptyRecyclerView;
import com.eyssyapps.fypcms.enumerations.LogoutType;
import com.eyssyapps.fypcms.managers.PreferencesManager;
import com.eyssyapps.fypcms.models.IdTokenResponse;
import com.eyssyapps.fypcms.models.LecturerPerson;
import com.eyssyapps.fypcms.models.NewsPost;
import com.eyssyapps.fypcms.models.RefreshTokenRequest;
import com.eyssyapps.fypcms.models.User;
import com.eyssyapps.fypcms.models.WebApiArguments;
import com.eyssyapps.fypcms.services.RegistrationIntentService;
import com.eyssyapps.fypcms.services.RetrofitProviderService;
import com.eyssyapps.fypcms.services.retrofit.AuthService;
import com.eyssyapps.fypcms.services.retrofit.LecturerService;
import com.eyssyapps.fypcms.services.retrofit.NewsService;
import com.eyssyapps.fypcms.utils.Constants;
import com.eyssyapps.fypcms.utils.threading.RunnableUtils;
import com.eyssyapps.fypcms.utils.view.SystemMessagingUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.net.SocketTimeoutException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LecturerMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    public static final int MODULES_START = 100,
        TIMETABLE_START = MODULES_START + 1,
        ACCOUNT_SETTINGS_START = TIMETABLE_START + 1;

    private User user;
    private LecturerPerson lecturer;
    private Retrofit retrofit;
    private LecturerService lecturerService;
    private AuthService authService;
    private NewsService newsService;
    private PreferencesManager sharedPreferences;
    private GoogleCloudMessaging gcm;

    private LinearLayoutManager layoutManager;
    private NewsRecyclerViewAdapter newsPostAdapter;
    private ProgressDialog progressDialog;

    private boolean initialNewsLoaded = false, initialLecturerLoaded = false;

    @Bind (R.id.toolbar)
    Toolbar toolbar;
    @Bind (R.id.lecturer_drawer_layout)
    DrawerLayout drawer;
    @Bind (R.id.lecturer_nav_view)
    NavigationView navigationView;

    private View headerLayout;
    private TextView nameText, usernameText;
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EmptyRecyclerView emptyRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(LecturerMainActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Making preparations...");
        progressDialog.show();

        gcm = GoogleCloudMessaging.getInstance(LecturerMainActivity.this);
        sharedPreferences = PreferencesManager.getInstance(LecturerMainActivity.this);

        RegistrationIntentService.checkRegistrationStatus(this, sharedPreferences);

        retrofit = RetrofitProviderService.getDefaultInstance();
        lecturerService = this.retrofit.create(LecturerService.class);
        authService = retrofit.create(AuthService.class);
        newsService = retrofit.create(NewsService.class);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        headerLayout = navigationView.getHeaderView(0);

        nameText = (TextView) headerLayout.findViewById(R.id.nav_header_name_text);
        usernameText = (TextView) headerLayout.findViewById(R.id.nav_header_username_text);

        coordinatorLayout = (CoordinatorLayout) drawer.findViewById(R.id.lecturer_coordinate_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) coordinatorLayout.findViewById(R.id.lecturer_swipe_refresh_layout);
        emptyRecyclerView = (EmptyRecyclerView) coordinatorLayout.findViewById(R.id.lecturer_recycler_view_empty_support);

        layoutManager = new LinearLayoutManager(LecturerMainActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        emptyRecyclerView.setLayoutManager(layoutManager);
        newsPostAdapter = new NewsRecyclerViewAdapter(this, emptyRecyclerView);
        emptyRecyclerView.setAdapter(newsPostAdapter);

        navigationView.setNavigationItemSelectedListener(LecturerMainActivity.this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                swipeRefreshLayout.setRefreshing(true);
                fetchNews();
            }
        });

        this.prepareUser();
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
                    logout(LogoutType.SECURITY);
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
        progressDialog.setMessage("Fetching data...");

        this.fetchLecturer();
        this.fetchNews();
    }

    private void prepareUser()
    {
        this.user = new User(
            sharedPreferences.getStringWithDefault(PreferencesManager.PREFS_DATA_USER_ID),
            sharedPreferences.getStringWithDefault(PreferencesManager.PREFS_DATA_USERNAME),
            sharedPreferences.getStringWithDefault(PreferencesManager.PREFS_DATA_USER_TYPE)
        );

        usernameText.setText(user.getUsername());
    }

    private void fetchLecturer()
    {
        WebApiArguments arguments = RetrofitProviderService.getStandardWebApiArguments(sharedPreferences);

        Call<LecturerPerson> call = lecturerService.getLecturer(arguments.getUserId(), arguments.getAuthorizedBearer());
        call.enqueue(new Callback<LecturerPerson>()
        {
            @Override
            public void onResponse(Call<LecturerPerson> call, Response<LecturerPerson> response)
            {
                if (RetrofitProviderService.checkOk(response))
                {
                    lecturer = response.body();
                    nameText.setText(lecturer.getPerson().getFullName());

                    reportLecturerLoaded();
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
                            if (RetrofitProviderService.checkNotFound(response))
                            {
                                logout(LogoutType.SECURITY);
                            }
                            else
                            {
                                RetrofitProviderService.replaceIdTokenWithLatest(sharedPreferences, response.body());

                                fetchLecturer();
                            }
                        }

                        @Override
                        public void onFailure(Call<IdTokenResponse> call, Throwable t)
                        {
                            SystemMessagingUtils.createToast(
                                LecturerMainActivity.this,
                                getString(R.string.reauthenticationg_required),
                                Toast.LENGTH_SHORT).show();

                            logout(LogoutType.SECURITY);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<LecturerPerson> call, Throwable t)
            {
                // retry until the api wakes up -> should never really happen in a real application
                if (t instanceof SocketTimeoutException)
                {
                    progressDialog.setMessage("API is asleep, retrying...");

                    fetchLecturer();
                }
                else
                {
                    // something is not right here, we logout
                    logout(LogoutType.SECURITY);
                }
            }
        });
    }

    private void fetchNews()
    {
        WebApiArguments arguments = RetrofitProviderService.getStandardWebApiArguments(sharedPreferences);

        Call<List<NewsPost>> call = newsService.getNews(arguments.getAuthorizedBearer());
        call.enqueue(new Callback<List<NewsPost>>() {
            @Override
            public void onResponse(Call<List<NewsPost>> call, Response<List<NewsPost>> response)
            {
                List<NewsPost> newsPosts = response.body();

                if (RetrofitProviderService.checkOk(response))
                {
                    if (!newsPosts.isEmpty())
                    {
                        newsPostAdapter.replaceCollection(newsPosts, true);
                    }

                    swipeRefreshLayout.setRefreshing(false);

                    reportNewsLoaded();
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
                            if (RetrofitProviderService.checkNotFound(response))
                            {
                                logout(LogoutType.SECURITY);
                            }
                            else
                            {
                                RetrofitProviderService.replaceIdTokenWithLatest(sharedPreferences, response.body());

                                fetchNews();
                            }
                        }

                        @Override
                        public void onFailure(Call<IdTokenResponse> call, Throwable t)
                        {
                            SystemMessagingUtils.createToast(
                                    LecturerMainActivity.this,
                                    getString(R.string.reauthenticationg_required),
                                    Toast.LENGTH_SHORT).show();

                            logout(LogoutType.SECURITY);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<NewsPost>> call, Throwable t)
            {
                if (t instanceof SocketTimeoutException)
                {
                    fetchNews();
                }
                else
                {
                    swipeRefreshLayout.setRefreshing(false);

                    SystemMessagingUtils.createSnackbar(emptyRecyclerView, t.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.lecturer_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings ("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.nav_modules:
                startActivityForResult(LecturerModulesActivity.class, MODULES_START);
                break;
            case R.id.nav_timetable:
                startActivityForResult(LecturerTimetableActivity.class, TIMETABLE_START);
                break;
            case R.id.nav_logout:
                logout(LogoutType.NORMAL);
                break;
            default:
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void reportLecturerLoaded()
    {
        initialLecturerLoaded = true;

        checkIsEverythingLoaded();
    }

    private void reportNewsLoaded()
    {
        initialNewsLoaded = true;

        checkIsEverythingLoaded();
    }

    private void checkIsEverythingLoaded()
    {
        if (initialLecturerLoaded && initialNewsLoaded)
        {
            progressDialog.dismiss();

            SystemMessagingUtils.createSnackbar(emptyRecyclerView, "Data loaded", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void clearSharedPreferences()
    {
        try
        {
            sharedPreferences.clear();

            Toast.makeText(LecturerMainActivity.this, "Shared preferences cleared!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {
            Toast.makeText(LecturerMainActivity.this, "Couldnt clear shared preferences", Toast.LENGTH_SHORT).show();
        }
    }

    private void logoutWithProgressDelay()
    {
        RunnableUtils.ExecuteWithDelay(new Runnable()
        {
            @Override
            public void run()
            {
                final ProgressDialog progressDialog = new ProgressDialog(LecturerMainActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Logging out...");
                progressDialog.show();

                logout(LogoutType.NORMAL);

                progressDialog.dismiss();
            }
        }, 200);
    }

    private void startActivityForResult(Class<?> cls, int requestCode)
    {
        Intent intent = new Intent(LecturerMainActivity.this, cls);

        startActivityForResult(intent, requestCode);
    }

    private void logout(LogoutType logoutType)
    {
        Intent intent = new Intent(LecturerMainActivity.this, LoginActivity.class);

        if (logoutType == LogoutType.SECURITY)
        {
            intent.putExtra(Constants.SECURITY_LOGOUT, true);
        }

        sharedPreferences.putBoolean(PreferencesManager.PREFS_DATA_LOGGED_IN, false);
        startActivity(intent);

        finish();
    }
}