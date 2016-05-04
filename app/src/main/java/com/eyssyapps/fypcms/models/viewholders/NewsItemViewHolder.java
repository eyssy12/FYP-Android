package com.eyssyapps.fypcms.models.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eyssyapps.fypcms.R;

/**
 * Created by eyssy on 25/04/2016.
 */
public class NewsItemViewHolder extends RecyclerView.ViewHolder
{
    private TextView titleText, timestampText, postedByText;
    private ImageView readMore;

    public NewsItemViewHolder(View itemView)
    {
        super(itemView);

        titleText = (TextView) itemView.findViewById(R.id.newspost_title);
        timestampText = (TextView) itemView.findViewById(R.id.newspost_timestamp);
        postedByText = (TextView) itemView.findViewById(R.id.newspost_postedBy);
        readMore = (ImageView) itemView.findViewById(R.id.read_more);
    }

    public ImageView getReadMore()
    {
        return readMore;
    }

    public TextView getTitleText()
    {
        return titleText;
    }

    public TextView getTimestampText()
    {
        return timestampText;
    }

    public TextView getPostedByText()
    {
        return postedByText;
    }
}