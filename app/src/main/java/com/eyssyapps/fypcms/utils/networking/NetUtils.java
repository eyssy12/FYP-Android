package com.eyssyapps.fypcms.utils.networking;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.msebera.android.httpclient.HttpStatus;

/**
 * Created by eyssy on 01/03/2016.
 */
public class NetUtils
{
    public static final String GET = "GET";

    public static String getContentFromStream(String url)
    {
        StringBuilder builder = null;
        try
        {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod(NetUtils.GET);

            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            builder = new StringBuilder(inputStream.available());
            String line;
            while ((line = reader.readLine()) != null)
            {
                builder.append(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return builder == null
                ? null
                : builder.toString();
    }

    public static InputStream retrieveImageStream(String url)
    {
        HttpURLConnection urlConnection = null;
        try
        {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpStatus.SC_OK)
            {
                return null;
            }

            return urlConnection.getInputStream();
        }
        catch (Exception e)
        {
            Log.w("ImageDownloader", "Error retrieving stream from " + url);
        }
        finally
        {
            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    public static Bitmap downloadBitmap(String url)
    {
        InputStream inputStream = retrieveImageStream(url);

        if (inputStream != null)
        {
            return BitmapFactory.decodeStream(inputStream);
        }

        return null;
    }

    public static Drawable downloadBitmapAsDrawable(String url)
    {
        InputStream inputStream = retrieveImageStream(url);
        return Drawable.createFromStream(inputStream, "src");
    }
}
