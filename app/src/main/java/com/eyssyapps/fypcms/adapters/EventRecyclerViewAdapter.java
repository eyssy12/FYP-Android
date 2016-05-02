package com.eyssyapps.fypcms.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eyssyapps.fypcms.R;
import com.eyssyapps.fypcms.enumerations.TimetableType;
import com.eyssyapps.fypcms.models.Event;
import com.eyssyapps.fypcms.models.viewholders.EventItemViewHolder;
import com.eyssyapps.fypcms.utils.Constants;
import com.eyssyapps.fypcms.utils.view.SystemMessagingUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by eyssy on 08/04/2016.
 */
public class EventRecyclerViewAdapter extends RecyclerViewAdapterBase<Event, EventItemViewHolder>
{
    private TimetableType timetableType;

    public EventRecyclerViewAdapter(Context context, View parentView, TimetableType timetableType)
    {
        super(context, parentView);

        this.timetableType = timetableType;
    }

    @Override
    public void replaceCollection(List<Event> collection, boolean immediateRefresh)
    {
        synchronized (mutex)
        {
            if (!collection.isEmpty())
            {
                items = new LinkedList<>(collection);
                Collections.sort(items);
            }
        }

        if (immediateRefresh)
        {
            refresh();
        }
    }

    @Override
    public EventItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.recycler_view_item_event, parent, false);

        return new EventItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventItemViewHolder holder, int position)
    {
        final Event item = items.get(position);

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        String startFormatted;
        String endFormatted;

        try
        {
            start.setTime(Constants.DEFAULT_SIMPLE_DATE_FORMAT.parse(item.getStart()));
            end.setTime(Constants.DEFAULT_SIMPLE_DATE_FORMAT.parse(item.getEnd()));

            startFormatted = Constants.DEFAULT_EVENT_DISPLAY_SIMPLE_DATE_FORMAT.format(start.getTime());
            endFormatted = Constants.DEFAULT_EVENT_DISPLAY_SIMPLE_DATE_FORMAT.format(end.getTime());

            String formatted = startFormatted + " - " + endFormatted;

            holder.getEventNameText().setText(item.getTitle());
            holder.getEventTimeText().setText(formatted);

            if (timetableType == TimetableType.LECTURER)
            {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        String[] items = new String[] {"Cancel", "Modify" };

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder
                            .setTitle("Choose action for selected event")
                            .setItems(items, new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which)
                               {
                                   if (which == 0)
                                   {
                                       SystemMessagingUtils.showToast(context, "Cancelled " + item.getId(), Toast.LENGTH_SHORT);
                                   }
                                   else if (which == 1)
                                   {
                                       SystemMessagingUtils.showToast(context, "Not supported in v1.0", Toast.LENGTH_SHORT);
                                   }
                               }
                            })
                            .setNegativeButton("Dismiss", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                    }
                });
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }
}