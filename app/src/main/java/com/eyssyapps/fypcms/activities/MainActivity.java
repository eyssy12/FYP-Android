package com.eyssyapps.fypcms.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eyssyapps.fypcms.R;
import com.eyssyapps.fypcms.managers.PreferencesManager;
import com.eyssyapps.fypcms.models.User;
import com.eyssyapps.fypcms.utils.Constants;
import com.eyssyapps.fypcms.utils.threading.RunnableUtils;
import com.eyssyapps.fypcms.utils.view.SystemMessagingUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by eyssy on 29/02/2016.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.coordinate_layout)
    CoordinatorLayout coordinatorLayout;

    View headerLayout;
    TextView nameText, usernameText;
    ImageView profileImage;

    private User user;
    private Retrofit retrofit;
    private PreferencesManager sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        ButterKnife.bind(this);

        toolbar.setElevation(50);
        setSupportActionBar(toolbar);

        this.retrofit = new Retrofit.Builder()
            .baseUrl(Constants.API_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        this.sharedPreferences = PreferencesManager.getInstance(this);
        this.user = new User(getIntent());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        headerLayout = navigationView.getHeaderView(0);

        profileImage = (ImageView) headerLayout.findViewById(R.id.profileImage);

        nameText = (TextView) headerLayout.findViewById(R.id.nav_header_name_text);
        nameText.setText("Name");

        usernameText = (TextView) headerLayout.findViewById(R.id.nav_header_username_text);
        usernameText.setText(user.getUsername());

        navigationView.setNavigationItemSelectedListener(this);
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
                showUserSettings();
                break;
            case R.id.nav_logout:
                logout();
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

    protected void showUserSettings()
    {
        //startActivity(new Intent(getBaseContext(), UserSettingsActivity.class));
        SystemMessagingUtils.showToast(MainActivity.this, "to be implemented", Toast.LENGTH_SHORT);
    }

    protected void logout()
    {
        RunnableUtils.ExecuteWithDelay(new Runnable()
        {
            @Override
            public void run()
            {
            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Logging out...");
            progressDialog.show();

            sharedPreferences.putBoolean(PreferencesManager.PREFS_DATA_LOGGED_IN, false);

            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();

            // onLoginFailed();
            progressDialog.dismiss();
            }
        }, 200);
    }
}
