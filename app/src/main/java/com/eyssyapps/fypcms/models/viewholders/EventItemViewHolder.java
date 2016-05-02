package com.eyssyapps.fypcms.models.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.eyssyapps.fypcms.R;

/**
 * Created by eyssy on 01/05/2016.
 */
public class EventItemViewHolder extends RecyclerView.ViewHolder
{
    private TextView eventNameText,
        eventTimeText;

    public EventItemViewHolder(View itemView)
    {
        super(itemView);

        eventNameText = (TextView) itemView.findViewById(R.id.eventNameText);
        eventTimeText = (TextView) itemView.findViewById(R.id.eventTimeText);
    }

    public TextView getEventNameText()
    {
        return eventNameText;
    }

    public TextView getEventTimeText()
    {
        return eventTimeText;
    }
}
