package com.eyssyapps.fypcms.models;

import com.eyssyapps.fypcms.utils.Constants;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by eyssy on 02/05/2016.
 */
public class CancelledEvent
{
    private int cancellationEventId;

    private String timestamp,
        cancelledBy,
        cancelledEventStart,
        cancelledEventEnd,
        cancelledEventTitle;

    public CancelledEvent(String timestamp, String cancelledBy, String cancelledEventTitle, String cancelledEventStart, String cancelledEventEnd)
    {
        this("-1", timestamp, cancelledBy, cancelledEventTitle, cancelledEventStart, cancelledEventEnd);
    }

    public CancelledEvent(int cancellationEventId, String timestamp, String cancelledBy, String cancelledEventTitle)
    {
        this.cancellationEventId = cancellationEventId;
        this.timestamp = timestamp;
        this.cancelledBy = cancelledBy;
        this.cancelledEventTitle = cancelledEventTitle;
    }

    public CancelledEvent(String cancellationEventId, String timestamp, String cancelledBy, String cancelledEventTitle, String cancelledEventStart, String cancelledEventEnd)
    {
        this.cancellationEventId = Integer.valueOf(cancellationEventId);
        this.timestamp = timestamp;
        this.cancelledBy = cancelledBy;
        this.cancelledEventTitle = cancelledEventTitle;
        this.cancelledEventStart = cancelledEventStart;
        this.cancelledEventEnd = cancelledEventEnd;
    }

    public int getCancellationEventId()
    {
        return cancellationEventId;
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public String getCancelledBy()
    {
        return cancelledBy;
    }

    public String getCancelledEventTitle()
    {
        return cancelledEventTitle;
    }

    public String getCancelledEventStart()
    {
        return cancelledEventStart;
    }

    public String getCancelledEventEnd()
    {
        return cancelledEventEnd;
    }

    public String getFormattedStartAndEndDate()
    {
        String formatted = "";

        try
        {
            Date start = Constants.DEFAULT_SIMPLE_DATE_FORMAT.parse(cancelledEventStart);
            Date end = Constants.DEFAULT_SIMPLE_DATE_FORMAT.parse(cancelledEventEnd);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);

            String dayOfWeek = Constants.DAYS_OF_WEEK.get(calendar.get(Calendar.DAY_OF_WEEK) - 1);

            formatted = "(" + dayOfWeek + " " + Constants.DEFAULT_EVENT_DISPLAY_SIMPLE_DATE_FORMAT.format(start) + " - " + Constants.DEFAULT_EVENT_DISPLAY_SIMPLE_DATE_FORMAT.format(end) +")";
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return formatted;
    }
}