package com.eyssyapps.fypcms.models;

import com.eyssyapps.fypcms.utils.Constants;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by eyssy on 05/04/2016.
 */
public class Event implements Comparable<Event>
{
    private int id;
    private String title, room, start, end;
    private boolean repeatable;

    private Date startDate,
        endDate;

    public Event(int id, String title, String room, String start, String end, boolean repeatable)
    {
        this.id = id;
        this.title = title;
        this.room = room;
        this.start = start;
        this.end = end;
        this.repeatable = repeatable;

        try
        {
            this.startDate = Constants.DEFAULT_SIMPLE_DATE_FORMAT.parse(start);
            this.endDate = Constants.DEFAULT_SIMPLE_DATE_FORMAT.parse(end);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    public int getId()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }

    public String getRoom()
    {
        return room;
    }

    public String getStart()
    {
        return start;
    }

    public String getEnd()
    {
        return end;
    }

    public boolean isRepeatable()
    {
        return repeatable;
    }

    public Calendar getStartTimeAsCalendar()
    {
        return getDateAsCalendar(startDate);
    }

    public Calendar getEndTimeAsCalendar()
    {
        return getDateAsCalendar(endDate);
    }

    private Calendar getDateAsCalendar(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar;
    }

    @Override
    public int compareTo(Event another)
    {
        Calendar startThis = Calendar.getInstance();
        Calendar startAnother = Calendar.getInstance();

        try
        {
            startThis.setTime(Constants.DEFAULT_SIMPLE_DATE_FORMAT.parse(start));
            startAnother.setTime(Constants.DEFAULT_SIMPLE_DATE_FORMAT.parse(another.start));

            this.checkLunchTime(startThis);
            this.checkLunchTime(startAnother);

            if (startThis.before(startAnother))
            {
                return -1;
            }

            if (startThis.after(startAnother))
            {
                return 1;
            }

            // they're the same date
            return 0;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    private void checkLunchTime(Calendar calendar)
    {
        if (calendar.get(Calendar.HOUR) == 0)
        {
            calendar.set(Calendar.HOUR, 12);
        }
    }
}