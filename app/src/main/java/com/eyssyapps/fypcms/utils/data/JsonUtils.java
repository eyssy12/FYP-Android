package com.eyssyapps.fypcms.utils.data;

import android.util.Log;

import com.eyssyapps.fypcms.utils.networking.NetUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eyssy on 01/03/2016.
 */
public class JsonUtils
{
    public static final String ARRAY_IDENTIFIER = "[",
            OBJECT_IDENTIFIER = "{";

    private static Gson GSON = new Gson();

    public static JSONObject getJSONfromURL(String url)
    {
        String content = NetUtils.getContentFromStream(url);

        JSONObject jsonData = null;
        try
        {
            jsonData = new JSONObject(content);
        }
        catch (JSONException e)
        {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        return jsonData;
    }

    public static boolean isEmptyArray(String json)
    {
        if (json.equals("[]"))
        {
            return true;
        }

        return false;
    }

    public static boolean isJsonArray(String json)
    {
        if (json.startsWith(ARRAY_IDENTIFIER))
        {
            return true;
        }

        return false;
    }

    public static boolean isJsonObject(String json)
    {
        if (json.startsWith(OBJECT_IDENTIFIER))
        {
            return true;
        }

        return false;
    }

    public static JsonArray asJsonArray(String data)
    {
        if (data == null || data.isEmpty())
        {
            throw new NullPointerException("Passed in data has no reference or is empty");
        }

        return GSON.fromJson(data, JsonArray.class);
    }

    public static JsonObject asJsonObject(String data)
    {
        if (data == null || data.isEmpty())
        {
            throw new NullPointerException("Passed in data has no reference or is empty");
        }

        return GSON.fromJson(data, JsonObject.class);
    }

    public static <T> T parseAs(String json, Class<T> classOfT)
    {
        return GSON.fromJson(json, classOfT);
    }

    public static int[] getIntegerArrayFromJsonArray(JsonArray array)
    {
        int[] integers = new int[array.size()];

        int index = 0;
        for (JsonElement element : array)
        {
            integers[index] = element.getAsInt();
            index++;
        }

        return integers;
    }

    public static List<Integer> getIntegerCollectionFromJsonArray(JsonArray array)
    {
        ArrayList<Integer> integers = new ArrayList<>(array.size());
        for (JsonElement element : array)
        {
            integers.add(element.getAsInt());
        }

        return integers;
    }
}