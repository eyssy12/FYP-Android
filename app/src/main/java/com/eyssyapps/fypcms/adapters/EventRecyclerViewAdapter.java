package com.eyssyapps.fypcms.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eyssyapps.fypcms.R;
import com.eyssyapps.fypcms.models.Event;
import com.eyssyapps.fypcms.utils.Constants;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by eyssy on 08/04/2016.
 */
public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.EventItemViewHolder>
{
    private Context context;
    private LayoutInflater inflater;
    private LinkedList<Event> items;
    private final View parentView;

    private int selectedPosition = -1;
    private Object mutex;

    public EventRecyclerViewAdapter(Context context, View parentView)
    {
        this.context = context;
        this.items = new LinkedList<>();
        this.inflater = LayoutInflater.from(context);
        this.parentView = parentView;

        this.mutex = new Object();
    }

    public View getParentView()
    {
        return this.parentView;
    }

    public boolean addAtPosition(Event item, int position)
    {
        if (items.contains(item) || position > items.size())
        {
            return false;
        }

        items.add(position, item);
        refresh();

        return true;
    }

    public boolean add(Event item)
    {
        if (items.contains(item))
        {
            return false;
        }

        items.addFirst(item);
        refresh();

        return true;
    }

    public void addNewCollection(List<Event> collection, boolean immediateRefresh)
    {
        items.clear();
        addCollection(collection, immediateRefresh);
    }

    public void replaceCollection(List<Event> newCollection, boolean immediateRefresh)
    {
        synchronized (mutex)
        {
            if (!newCollection.isEmpty())
            {
                items = new LinkedList<>(newCollection);
                Collections.sort(items);
            }
        }

        if (immediateRefresh)
        {
            refresh();
        }
    }

    public void addCollection(List<Event> collection, boolean immediateRefresh)
    {
        synchronized (mutex)
        {
            if (!collection.isEmpty())
            {
                for (Event event : collection)
                {
                    items.addFirst(event);
                }
            }
        }

        if (immediateRefresh)
        {
            refresh();
        }
    }

    public void clear()
    {
        items.clear();
        refresh();
    }

    public boolean remove(String item)
    {
        if (items.contains(item))
        {
            items.remove(item);
            refresh();
            return true;
        }

        return false;
    }

    public void refresh()
    {
        notifyDataSetChanged();
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

            holder.eventNameText.setText(item.getTitle());
            holder.eventTimeText.setText(formatted);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    class EventItemViewHolder extends RecyclerView.ViewHolder
    {
        TextView eventNameText,
            eventTimeText;

        public EventItemViewHolder(View itemView)
        {
            super(itemView);

            eventNameText = (TextView) itemView.findViewById(R.id.eventNameText);
            eventTimeText = (TextView) itemView.findViewById(R.id.eventTimeText);
        }
    }
}
