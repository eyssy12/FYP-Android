package com.eyssyapps.fypcms.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.eyssyapps.fypcms.Protocol;
import com.eyssyapps.fypcms.R;
import com.eyssyapps.fypcms.activities.lecturer.LecturerMainActivity;
import com.eyssyapps.fypcms.activities.student.StudentMainActivity;
import com.eyssyapps.fypcms.managers.PreferencesManager;
import com.eyssyapps.fypcms.models.AuthRequest;
import com.eyssyapps.fypcms.models.TokenData;
import com.eyssyapps.fypcms.models.User;
import com.eyssyapps.fypcms.services.RetrofitProviderService;
import com.eyssyapps.fypcms.services.retrofit.AuthService;
import com.eyssyapps.fypcms.utils.Constants;
import com.eyssyapps.fypcms.utils.data.JsonUtils;
import com.eyssyapps.fypcms.utils.view.SystemMessagingUtils;
import com.eyssyapps.fypcms.utils.view.ViewUtils;
import com.google.gson.JsonObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by eyssy on 26/02/2016.
 */
public class LoginActivity extends AppCompatActivity
{
    public static final int REQUEST_SIGNUP = 0,
            REQUEST_LOGGED_OUT = REQUEST_SIGNUP + 1;

    private static final String TAG = "LoginActivity";

    private boolean rememberUser = false,
            loggedIn = false;

    private PreferencesManager manager;
    private Retrofit retrofit;
    private User user;

    @Bind(R.id.input_username)
    EditText usernameText;
    @Bind(R.id.input_password)
    EditText passwordText;
    @Bind(R.id.btn_login)
    Button loginButton;
    @Bind(R.id.chk_remember)
    CheckBox rememberChk;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            if (extras.containsKey(Constants.SECURITY_LOGOUT) && extras.getBoolean(Constants.SECURITY_LOGOUT))
            {
                SystemMessagingUtils.showToast(LoginActivity.this, "Due to security reasons, please re-authenticate yourself", Toast.LENGTH_SHORT);
            }
        }

        manager = PreferencesManager.getInstance(this);
        user = new User();

        retrofit = RetrofitProviderService.getDefaultInstance();

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                login();
            }
        });

        rememberChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                rememberUser = isChecked;
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        // disable going back to the SendMessageActivity
        moveTaskToBack(true);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        savePreferences();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        loadPreferences();

        //
        if (loggedIn)
        {
            startActivityForUserType();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_LOGGED_OUT)
        {
            if (resultCode == RESULT_OK)
            {
                loggedIn = false;
            }
        }
    }

    private void savePreferences()
    {
        manager.putString(PreferencesManager.PREFS_DATA_USERNAME, user.getUsername());
        manager.putString(PreferencesManager.PREFS_DATA_USER_TYPE, user.getUserType());
        manager.putString(PreferencesManager.PREFS_DATA_USER_ID, String.valueOf(user.getUserId()));
        manager.putBoolean(PreferencesManager.PREFS_DATA_LOGGED_IN, loggedIn);
        manager.putBoolean(PreferencesManager.PREFS_DATA_REMEMBER, rememberUser);

        saveTokenDataToPreferences();
    }

    private void saveTokenDataToPreferences()
    {
        if (user.getTokenData() != null)
        {
            String recentlyUsedEntityID = manager.getStringWithDefault(PreferencesManager.PREFS_DATA_ENTITY_ID);

            if (recentlyUsedEntityID.equals(""))
            {
                // normal registration will be executed instead
                // reregistration indicates that we already have a record in the back end hence
                // if we tried to reregister, we are attempting to update a record (which doesnt exist, hence an exception will occur)
                manager.putBoolean(PreferencesManager.PREFS_DATA_DO_GCM_REREGISTRATION, false);
            }
            else
            {
                if (!recentlyUsedEntityID.equals(user.getTokenData().getEntityId()))
                {
                    manager.putBoolean(PreferencesManager.PREFS_DATA_DO_GCM_REREGISTRATION, true);
                }
                else
                {
                    manager.putBoolean(PreferencesManager.PREFS_DATA_DO_GCM_REREGISTRATION, false);
                }
            }
            
            manager.putString(PreferencesManager.PREFS_DATA_ENTITY_ID, user.getTokenData().getEntityId());
            manager.putString(PreferencesManager.PREFS_DATA_ID_TOKEN, user.getTokenData().getIdToken());
            manager.putString(PreferencesManager.PREFS_DATA_ID_TOKEN_EXPIRY, user.getTokenData().getIdTokenExpiry());
            manager.putString(PreferencesManager.PREFS_DATA_ACCESS_TOKEN, user.getTokenData().getAccessToken());
            manager.putString(PreferencesManager.PREFS_DATA_ACCESS_TOKEN_EXPIRY, user.getTokenData().getAccessTokenExpiry());
        }
    }

    protected void loadPreferences()
    {
        rememberUser = manager.getBoolean(
            PreferencesManager.PREFS_DATA_REMEMBER,
            PreferencesManager.PREFS_DEFAULT_BOOLEAN_VALUE);

        loggedIn = manager.getBoolean(
            PreferencesManager.PREFS_DATA_LOGGED_IN,
            PreferencesManager.PREFS_DEFAULT_BOOLEAN_VALUE);

        rememberChk.setChecked(rememberUser);

        if (rememberUser)
        {
            user.setUsername(manager.getString(PreferencesManager.PREFS_DATA_USERNAME, PreferencesManager.PREFS_DEFAULT_STRING_VALUE));
            usernameText.setText(user.getUsername());
        }
    }

    public void login()
    {
        Log.d(TAG, "Login");

        if (validateFields())
        {
            loginButton.setEnabled(false);

            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();

            //https://www.metachris.com/2015/10/retrofit-2-samples/
            AuthService service = retrofit.create(AuthService.class);

            Call<ResponseBody> call = service.authenticate(new AuthRequest(
                this.usernameText.getText().toString(),
                this.passwordText.getText().toString()));

            // enqueue indicates that it will be an async operation
            call.enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                {
                    try
                    {
                        ResponseBody result = response.body();

                        if (result == null)
                        {
                            onLoginFailed("Invalid credentials");
                            progressDialog.dismiss();
                        }
                        else
                        {
                            String bodyData = response.body().string();

                            JsonObject jsonObject = JsonUtils.asJsonObject(bodyData);
                            boolean authenticated = jsonObject.get(Protocol.AUTHENTICATED).getAsBoolean();
                            if (authenticated)
                            {
                                String entityId = jsonObject.get(TokenData.ENTITY_ID).getAsString();
                                String idToken = jsonObject.get(TokenData.ID_TOKEN).getAsString();
                                String idTokenExpiry = jsonObject.get(TokenData.ID_TOKEN_EXPIRY).getAsString();
                                String accessToken = jsonObject.get(TokenData.ACCESS_TOKEN).getAsString();
                                String accessTokenExpiry = jsonObject.get(TokenData.ACCESS_TOKEN_EXPIRY).getAsString();
                                String userType = jsonObject.get(User.USER_TYPE).getAsString();

                                int userId = -1; // admin
                                if (userType.equals(Protocol.STUDENT))
                                {
                                    userId = jsonObject.get(User.STUDENT_ID).getAsInt();
                                }
                                else if (userType.equals(Protocol.LECTURER))
                                {
                                    userId = jsonObject.get(User.LECTURER_ID).getAsInt();
                                }

                                TokenData tokenData = new TokenData(entityId, idToken, idTokenExpiry, accessToken, accessTokenExpiry);

                                user.setUsername(usernameText.getText().toString());
                                user.setUserType(userType);
                                user.setUserId(String.valueOf(userId));
                                user.setTokenData(tokenData);

                                loggedIn = true;

                                startActivityForUserType();
                            }
                            else
                            {
                                onLoginFailed("Invalid credentials.");
                                progressDialog.dismiss();
                            }
                        }
                    }
                    catch (IOException e)
                    {
                        SystemMessagingUtils.showToast(
                            LoginActivity.this,
                            "There is a problem with the login service.",
                            Toast.LENGTH_SHORT);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {
                    onLoginFailed("There is a problem with the login service.");
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void onLoginFailed(String message)
    {
        SystemMessagingUtils.createToast(
            LoginActivity.this,
            message,
            Toast.LENGTH_SHORT).show();

        loginButton.setEnabled(true);
    }

    public boolean validateFields()
    {
        boolean valid = false;

        if (ViewUtils.isValid(usernameText, "Username cannot be empty") &&
            ViewUtils.isValid(passwordText, "Password cannot be empty"))
        {
            valid = true;
        }

        return valid;
    }

    private void startActivityForUserType()
    {
        Intent intent;

        if (user.getUserType().equals(""))
        {
            user.setUserType(manager.getStringWithDefault(PreferencesManager.PREFS_DATA_USER_TYPE));
        }

        if (user.getUserType().equals(Protocol.STUDENT))
        {
            intent = new Intent(this, StudentMainActivity.class);
        }
        else
        {
            intent = new Intent(this, LecturerMainActivity.class);
        }

        // supply the user data
        intent.putExtra(User.USER_TYPE, user.getUserType());
        intent.putExtra(User.USER_ID, user.getUserId());
        intent.putExtra(User.USERNAME, user.getUsername());

        startActivity(intent);
        finish();
    }
}