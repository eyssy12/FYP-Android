package com.eyssyapps.fypcms.managers;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager
{
    public static final String PREFS_DEFAULT_STRING_VALUE = "",
        PREFS_NAME = "com.eyssyapps.fypcms.preferences",
        PREFS_DATA_USERNAME = "com.eyssyapps.fypcms.password",
        PREFS_DATA_REMEMBER = "com.eyssyapps.fypcms.user_remember",
        PREFS_DATA_USER_TYPE = "com.eyssyapps.fypcms.user_type",
        PREFS_DATA_USER_ID = "com.eyssyapps.fypcms.user_id",
        PREFS_DATA_LOGGED_IN = "com.eyssyapps.fypcms.user_logged_in",
        PREFS_DATA_ID_TOKEN = "com.eyssyapps.fypcms.id_token",
        PREFS_DATA_ID_TOKEN_EXPIRY = "com.eyssyapps.fypcms.id_token_expiry",
        PREFS_DATA_ACCESS_TOKEN = "com.eyssyapps.fypcms.access_token",
        PREFS_DATA_ACCESS_TOKEN_EXPIRY = "com.eyssyapps.fypcms.access_token_expiry",
        PREFS_DATA_ENTITY_ID = "com.eyssyapps.fypcms.entity_id",
        PREFS_DATA_DO_GCM_REREGISTRATION = "com.eyssyapps.fypcms.reregister";

    public static final boolean PREFS_DEFAULT_BOOLEAN_VALUE = false;

    private static PreferencesManager instance;
    private final SharedPreferences preferences;

    private PreferencesManager(Context context)
    {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void initializeInstance(Context context)
    {
        if (instance == null)
        {
            instance = new PreferencesManager(context);
        }
    }

    public static synchronized PreferencesManager getInstance(Context context)
    {
        if (instance == null)
        {
            initializeInstance(context);
        }

        return instance;
    }

    public void putString(String key, String value)
    {
        preferences.edit().putString(key, value).apply();
    }

    public void putBoolean(String key, boolean value)
    {
        preferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultValue)
    {
        return preferences.getBoolean(key, defaultValue);
    }

    public String getString(String key, String defaultValue)
    {
        return preferences.getString(key, defaultValue);
    }

    public String getStringWithDefault(String key)
    {
        return preferences.getString(key, PreferencesManager.PREFS_DEFAULT_STRING_VALUE);
    }

    public void remove(String key)
    {
        preferences.edit().remove(key).apply();
    }

    public boolean clear()
    {
        return preferences.edit().clear().commit();
    }
}