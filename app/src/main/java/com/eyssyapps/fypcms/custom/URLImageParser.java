package com.eyssyapps.fypcms.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.view.View;

import com.eyssyapps.fypcms.utils.networking.NetUtils;

/**
 * Created by eyssy on 25/04/2016.
 */
public class URLImageParser implements Html.ImageGetter
{
    private Context context;
    private View container;

    public URLImageParser(View container, Context context)
    {
        this.context = context;
        this.container = container;
    }

    public Drawable getDrawable(String source)
    {
        URLDrawable urlDrawable = new URLDrawable();

        // get the actual source
        ImageGetterAsyncTask asyncTask = new ImageGetterAsyncTask( urlDrawable);
        asyncTask.execute(source);

        return urlDrawable;
    }

    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable>
    {
        private URLDrawable urlDrawable;

        public ImageGetterAsyncTask(URLDrawable urlDrawable)
        {
            this.urlDrawable = urlDrawable;
        }

        @Override
        protected Drawable doInBackground(String... params)
        {
            String source = params[0];

            return fetchDrawable(source);
        }

        @Override
        protected void onPostExecute(Drawable result)
        {
            // set the correct bound according to the result from HTTP call
            urlDrawable.setBounds(0, 0, result.getIntrinsicWidth(), result.getIntrinsicHeight());

            // change the reference of the current drawable to the result
            // from the HTTP call
            urlDrawable.drawable = result;

            // redraw the image by invalidating the container
            URLImageParser.this.container.invalidate();
        }

        public Drawable fetchDrawable(String urlString)
        {
            try
            {
                Drawable drawable = NetUtils.downloadBitmapAsDrawable(urlString);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }
            catch (Exception e)
            {
                return null;
            }
        }
    }
}