package com.eyssyapps.fypcms.services;

import com.eyssyapps.fypcms.managers.PreferencesManager;
import com.eyssyapps.fypcms.models.IdTokenResponse;
import com.eyssyapps.fypcms.models.WebApiArguments;
import com.eyssyapps.fypcms.utils.Constants;

import java.net.HttpURLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by eyssy on 05/04/2016.
 */
public class RetrofitProviderService
{
    public static final String HEADER_AUTHORIZATION = "Authorization";

    private static final String BEARER = "Bearer" + " ";
    private static Retrofit retrofit;

    private static void initializeRetrofit(String apiBase, List<Converter.Factory> converterFactories)
    {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(apiBase);

        for (Converter.Factory factory : converterFactories)
        {
            builder.addConverterFactory(factory);
        }

        retrofit = builder.build();
    }

    public static synchronized Retrofit getCustomInstance(String apiBase, List<Converter.Factory> converterFactories)
    {
        if (retrofit == null)
        {
            initializeRetrofit(apiBase, converterFactories);
        }

        return retrofit;
    }

    public static synchronized Retrofit getDefaultInstance()
    {
        if (retrofit == null)
        {
            ArrayList<Converter.Factory> factories = new ArrayList<>();
            factories.add(GsonConverterFactory.create());

            initializeRetrofit(Constants.API_BASE, factories);
        }

        return retrofit;
    }

    public static String buildBearerAuthorizationHeader(String token)
    {
        return RetrofitProviderService.BEARER + token;
    }

    public static boolean isTokenExpired(final PreferencesManager sharedPreferences)
    {
        String expiryDateString = sharedPreferences.getStringWithDefault(PreferencesManager.PREFS_DATA_ID_TOKEN_EXPIRY);

        Date expiryDate = Calendar.getInstance(Constants.DEFAULT_TIMEZONE).getTime();
        Date currentTime = Calendar.getInstance(Constants.DEFAULT_TIMEZONE).getTime();

        try
        {
            expiryDate = Constants.DEFAULT_SIMPLE_DATE_FORMAT.parse(expiryDateString);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        Calendar expiryDateCalendar = Calendar.getInstance();
        expiryDateCalendar.setTime(expiryDate);
        expiryDateCalendar.add(Calendar.HOUR, 1); // +1 to match current time

        Calendar nowCalendar = Calendar.getInstance(Constants.DEFAULT_TIMEZONE);
        nowCalendar.setTime(currentTime);

        if (nowCalendar.after(expiryDateCalendar)) // get a new token from a refresh token
        {
            return false;
        }

        return true;
    }

    public static void replaceIdTokenWithLatest(PreferencesManager sharedPreferences, IdTokenResponse response)
    {
        sharedPreferences.putString(PreferencesManager.PREFS_DATA_ID_TOKEN, response.getIdToken());
        sharedPreferences.putString(PreferencesManager.PREFS_DATA_ID_TOKEN_EXPIRY, response.getIdTokenExpiry());
    }

    public static WebApiArguments getStandardWebApiArguments(PreferencesManager sharedPreferences)
    {
        int userId = Integer.parseInt(sharedPreferences.getStringWithDefault(PreferencesManager.PREFS_DATA_USER_ID));
        String token = sharedPreferences.getStringWithDefault(PreferencesManager.PREFS_DATA_ID_TOKEN);

        return new WebApiArguments(userId, token);
    }

    public static boolean checkOk(Response<?> response)
    {
        return response.raw().code() == HttpURLConnection.HTTP_OK;
    }

    public static boolean checkNewTokenRequired(Response<?> response)
    {
        return response.raw().code() == HttpURLConnection.HTTP_UNAUTHORIZED || checkNotFound(response);
    }

    public static boolean checkNotFound(Response<?> response)
    {
        return response.raw().code() == HttpURLConnection.HTTP_NOT_FOUND;
    }
}
