package com.eyssyapps.fypcms.models.viewholders;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.eyssyapps.fypcms.R;

/**
 * Created by eyssy on 25/04/2016.
 */
public class NewsItemViewHolder extends RecyclerView.ViewHolder
{
    private TextView titleText, bodyText, timestampText, postedByText;

    private WebView webView;

    public NewsItemViewHolder(View itemView)
    {
        super(itemView);

        // bodyText = (TextView) itemView.findViewById(R.id.newspost_body);
        //bodyText.setVisibility(View.GONE);

        titleText = (TextView) itemView.findViewById(R.id.newspost_title);
        timestampText = (TextView) itemView.findViewById(R.id.newspost_timestamp);
        postedByText = (TextView) itemView.findViewById(R.id.newspost_postedBy);



        webView = (WebView)itemView.findViewById(R.id.newspost_webview_body);

        if (Build.VERSION.SDK_INT >= 19)
        {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else
        {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public TextView getTitleText()
    {
        return titleText;
    }

    public TextView getBodyText()
    {
        return bodyText;
    }

    public TextView getTimestampText()
    {
        return timestampText;
    }

    public TextView getPostedByText()
    {
        return postedByText;
    }

    public WebView getWebView()
    {
        return webView;
    }
}