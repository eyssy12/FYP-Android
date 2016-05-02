package com.eyssyapps.fypcms.models;

/**
 * Created by eyssy on 02/05/2016.
 */
public class CancelledEvent
{
    private int id,
        cancellationEventId;

    private String timestamp,
        cancelledBy;

    public CancelledEvent(int cancellationEventId, String timestamp, String cancelledBy)
    {
        this(-1, cancellationEventId, timestamp, cancelledBy);
    }

    public CancelledEvent(int id, int cancellationEventId, String timestamp, String cancelledBy)
    {
        this.id = id;
        this.cancellationEventId = cancellationEventId;
        this.timestamp = timestamp;
        this.cancelledBy = cancelledBy;
    }

    public int getId()
    {
        return id;
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
}