package com.eyssyapps.fypcms.utils.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.eyssyapps.fypcms.adapters.EventRecyclerViewAdapter;
import com.eyssyapps.fypcms.custom.EmptyRecyclerView;
import com.eyssyapps.fypcms.enumerations.TimetableType;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * Created by eyssy on 04/12/2015.
 */
public class ViewUtils
{
    public static boolean isValid(EditText text)
    {
        return isValid(text, "Something is not right");
    }

    public static boolean isValid(EditText text, String errorMessage)
    {
        return isValid(text, errorMessage, null);
    }

    public static boolean isValid(EditText text, String errorMessage, Pattern pattern)
    {
        String value = text.getText().toString();

        if (value.isEmpty())
        {
            text.setError(errorMessage);
            return false;
        }
        else if (!(pattern == null || pattern.matcher(value).matches()))
        {
            text.setError(errorMessage);
            return false;
        }

        text.setError(null);
        return true;
    }

    public static void startImagePickerActivity(Activity context, int requestCode)
    {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        context.startActivityForResult(chooserIntent, requestCode);
    }

    public static Drawable drawableFromUrl(String url) throws IOException
    {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(x);
    }

    public static Pair<EmptyRecyclerView, EventRecyclerViewAdapter> createRecyclerViewAdapterPair(Context context, LayoutInflater inflater, TimetableType timetableType)
    {
        EmptyRecyclerView emptyRecyclerView = new EmptyRecyclerView(context);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        emptyRecyclerView.setLayoutManager(layoutManager);

        // View view = inflater.inflate(R.layout.empty_recycler_view_state_layout, null);

        EventRecyclerViewAdapter adapter = new EventRecyclerViewAdapter(context, emptyRecyclerView, timetableType);
        // emptyRecyclerView.setEmptyView(view);
        emptyRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(context).build());
        emptyRecyclerView.setVerticalScrollBarEnabled(true);
        emptyRecyclerView.setAdapter(adapter);

        return new Pair<>(emptyRecyclerView, adapter);
    }
}
