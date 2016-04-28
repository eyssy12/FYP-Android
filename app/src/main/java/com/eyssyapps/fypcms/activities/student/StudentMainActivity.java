package com.eyssyapps.fypcms.activities.student;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eyssyapps.fypcms.R;
import com.eyssyapps.fypcms.activities.LoginActivity;
import com.eyssyapps.fypcms.activities.SendMessageActivity;
import com.eyssyapps.fypcms.adapters.NewsRecyclerViewAdapter;
import com.eyssyapps.fypcms.custom.EmptyRecyclerView;
import com.eyssyapps.fypcms.managers.PreferencesManager;
import com.eyssyapps.fypcms.models.IdTokenResponse;
import com.eyssyapps.fypcms.models.NewsPost;
import com.eyssyapps.fypcms.models.RefreshTokenRequest;
import com.eyssyapps.fypcms.models.StudentPerson;
import com.eyssyapps.fypcms.models.User;
import com.eyssyapps.fypcms.models.WebApiArguments;
import com.eyssyapps.fypcms.services.RegistrationIntentService;
import com.eyssyapps.fypcms.services.RetrofitProviderService;
import com.eyssyapps.fypcms.services.retrofit.AuthService;
import com.eyssyapps.fypcms.services.retrofit.NewsService;
import com.eyssyapps.fypcms.services.retrofit.StudentService;
import com.eyssyapps.fypcms.utils.threading.RunnableUtils;
import com.eyssyapps.fypcms.utils.view.SystemMessagingUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class StudentMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    public static final int CLASSMATES_START = 1,
        MODULES_START = CLASSMATES_START + 1,
        TIMETABLE_START = MODULES_START + 1,
        ACCOUNT_SETTINGS_START = TIMETABLE_START + 1,
        SEND_MESSAGE_START = ACCOUNT_SETTINGS_START + 1;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.coordinate_layout)
    CoordinatorLayout coordinatorLayout;

    private View headerLayout;
    private TextView nameText, usernameText;
    private ImageView profileImage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EmptyRecyclerView emptyRecyclerView;
    private LinearLayoutManager layoutManager;
    private NewsRecyclerViewAdapter newsPostAdapter;
    private ProgressDialog progressDialog;

    private User user;
    private StudentPerson student;
    private Retrofit retrofit;
    private StudentService studentService;
    private AuthService authService;
    private NewsService newsService;
    private PreferencesManager sharedPreferences;
    private GoogleCloudMessaging gcm;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        ButterKnife.bind(this);

        toolbar.setElevation(50);
        setSupportActionBar(toolbar);

        gcm = GoogleCloudMessaging.getInstance(StudentMainActivity.this);
        sharedPreferences = PreferencesManager.getInstance(StudentMainActivity.this);

        RegistrationIntentService.checkRegistrationStatus(this, sharedPreferences);

        progressDialog = new ProgressDialog(StudentMainActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Making preparations...");
        progressDialog.show();

        retrofit = RetrofitProviderService.getDefaultInstance();
        studentService = this.retrofit.create(StudentService.class);
        authService = retrofit.create(AuthService.class);
        newsService = retrofit.create(NewsService.class);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            StudentMainActivity.this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        headerLayout = navigationView.getHeaderView(0);
        profileImage = (ImageView) headerLayout.findViewById(R.id.profileImage);

        nameText = (TextView) headerLayout.findViewById(R.id.nav_header_name_text);
        usernameText = (TextView) headerLayout.findViewById(R.id.nav_header_username_text);

        swipeRefreshLayout = (SwipeRefreshLayout) coordinatorLayout.findViewById(R.id.news_content);

        layoutManager = new LinearLayoutManager(StudentMainActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        emptyRecyclerView = (EmptyRecyclerView)swipeRefreshLayout.findViewById(R.id.recycler_view_empty_support);
        emptyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsPostAdapter = new NewsRecyclerViewAdapter(this, emptyRecyclerView);
        emptyRecyclerView.setAdapter(newsPostAdapter);

        navigationView.setNavigationItemSelectedListener(StudentMainActivity.this);

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
                   logout();
               }
           });
       }
       else
       {
           fetchData();
       }
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

    private void fetchData()
    {
        progressDialog.setMessage("Fetching data...");

        this.fetchStudent();
        this.fetchNews();
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
                            RetrofitProviderService.replaceIdTokenWithLatest(sharedPreferences, response.body());

                            fetchNews();
                        }

                        @Override
                        public void onFailure(Call<IdTokenResponse> call, Throwable t)
                        {
                            SystemMessagingUtils.showSnackBar(
                                navigationView,
                                getString(R.string.reauthenticationg_required),
                                Snackbar.LENGTH_SHORT);

                            logout();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<NewsPost>> call, Throwable t)
            {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void fetchStudent()
    {
        String token = sharedPreferences.getStringWithDefault(PreferencesManager.PREFS_DATA_ID_TOKEN);
        String bearerAuthorization = RetrofitProviderService.buildBearerAuthorizationHeader(token);

        int userId = Integer.parseInt(this.user.getUserId());

        Call<StudentPerson> call = studentService.getStudent(userId, bearerAuthorization);
        call.enqueue(new Callback<StudentPerson>()
        {
            @Override
            public void onResponse(Call<StudentPerson> call, Response<StudentPerson> response)
            {
                final Call<StudentPerson> outterCall = call;

                if (RetrofitProviderService.checkOk(response))
                {
                    student = response.body();

                    nameText.setText(student.getPerson().getFullName());
                    progressDialog.dismiss();

                    SystemMessagingUtils.showSnackBar(
                            emptyRecyclerView,
                            "Data loaded",
                            Toast.LENGTH_SHORT);
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
                            RetrofitProviderService.replaceIdTokenWithLatest(sharedPreferences, response.body());

                            fetchStudent();
                        }

                        @Override
                        public void onFailure(Call<IdTokenResponse> call, Throwable t)
                        {
                            SystemMessagingUtils.showToast(
                                StudentMainActivity.this,
                                getString(R.string.reauthenticationg_required),
                                Toast.LENGTH_SHORT);

                            logout();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<StudentPerson> call, Throwable t)
            {
                String message = t.getMessage();

                progressDialog.dismiss();

                SystemMessagingUtils.createToast(
                    StudentMainActivity.this,
                    message,
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == MODULES_START)
        {
            if (resultCode == RESULT_OK)
            {
            }
            else if (resultCode == RESULT_CANCELED)
            {
                logout();
            }
        }
        else if (requestCode == CLASSMATES_START)
        {
            if (resultCode == RESULT_CANCELED)
            {
                logout();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.nav_account:
                // show the account settings here
                // startActivityForResult(StudentAccountSettingsActivity.class, ACCOUNT_SETTINGS_START);
                SystemMessagingUtils.showToast(this, "Future work implementation", Toast.LENGTH_SHORT);
                break;
            case R.id.nav_classmates:
                startActivityForResult(StudentClassmatesActivity.class, CLASSMATES_START);
                break;
            case R.id.nav_modules:
                startActivityForResult(StudentModulesActivity.class, MODULES_START);
                break;
            case R.id.nav_timetable:
                startActivityForResult(StudentTimetableActivity.class, TIMETABLE_START);
                break;
            case R.id.nav_logout:
                logoutWithProgressDelay();
                break;
            default:
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_student_main_activity, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_settings:
                return true;
            case R.id.reset_shared_preferences:
                clearSharedPreferences();
                return true;
            case R.id.send_message_activity:
                startActivityForResult(SendMessageActivity.class, SEND_MESSAGE_START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    protected void onResume()
    {
        super.onResume();

        this.fetchNews();
    }

    protected void startActivityForResult(Class<?> cls, int requestCode)
    {
        Intent intent = new Intent(StudentMainActivity.this, cls);

        startActivityForResult(intent, requestCode);
    }

    private void logoutWithProgressDelay()
    {
        RunnableUtils.ExecuteWithDelay(new Runnable()
        {
            @Override
            public void run()
            {
            final ProgressDialog progressDialog = new ProgressDialog(StudentMainActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Logging out...");
            progressDialog.show();

            logout();

            // onLoginFailed();
            progressDialog.dismiss();
            }
        }, 200);
    }

    private void logout()
    {
        sharedPreferences.putBoolean(PreferencesManager.PREFS_DATA_LOGGED_IN, false);
        startActivity(new Intent(StudentMainActivity.this, LoginActivity.class));

        finish();
    }

    private void clearSharedPreferences()
    {
        try
        {
            sharedPreferences.clear();

            Toast.makeText(StudentMainActivity.this, "Shared preferences cleared!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {
            Toast.makeText(StudentMainActivity.this, "Couldnt clear shared preferences", Toast.LENGTH_SHORT).show();
        }
    }
}