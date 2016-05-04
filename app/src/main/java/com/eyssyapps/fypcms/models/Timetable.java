package com.eyssyapps.fypcms.models;

import com.eyssyapps.fypcms.utils.Constants;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by eyssy on 05/04/2016.
 */
public abstract class Timetable
{
    private List<Event> events;

    public Timetable(List<Event> events)
    {
        this.events = events;
    }

    public List<String> getEventsAsString()
    {
        ArrayList<String> test = new ArrayList<>();

        for (Event event : events)
        {
            test.add(event.getTitle());
        }

        return test;
    }

    public List<Event> getEvents()
    {
        if (events == null)
        {
            return new ArrayList<>();
        }

        return events;
    }

    public List<Event> getMondayEvents()
    {
        return this.filterByDayOfWeek(Calendar.MONDAY);
    }

    public List<Event> getTuesdayEvents()
    {
        return this.filterByDayOfWeek(Calendar.TUESDAY);
    }

    public List<Event> getWednesdayEvents()
    {
        return this.filterByDayOfWeek(Calendar.WEDNESDAY);
    }

    public List<Event> getThursdayEvents()
    {
        return this.filterByDayOfWeek(Calendar.THURSDAY);
    }

    public List<Event> getFridayEvents()
    {
        return this.filterByDayOfWeek(Calendar.FRIDAY);
    }

    public Event getEventById(int id)
    {
        for (Event event : events)
        {
            if (event.getId() == id)
            {
                return event;
            }
        }

        return null;
    }

    private List<Event> filterByDayOfWeek(int dayOfWeek)
    {
        List<Event> filtered = new ArrayList<>();

        for (Event event : events)
        {
            Calendar calendar = Calendar.getInstance();

            try
            {
                calendar.setTime(Constants.DEFAULT_SIMPLE_DATE_FORMAT.parse(event.getStart()));
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }

            if (calendar.get(Calendar.DAY_OF_WEEK) == dayOfWeek)
            {
                filtered.add(event);
            }
        }

        return filtered;
    }
}