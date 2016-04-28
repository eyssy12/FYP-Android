package com.eyssyapps.fypcms.models;

import com.eyssyapps.fypcms.utils.Constants;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by eyssy on 06/04/2016.
 */
public class NewsPost
{
    private String id, title, body, postedBy, timestamp;

    public NewsPost(String id, String title, String body, String postedBy, String timestamp)
    {
        this.id = id;
        this.title = title;
        this.body = body;
        this.postedBy = postedBy;
        this.timestamp = timestamp;
    }

    public String getId()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }

    public String getBody()
    {
        return body;
    }

    public String getPostedBy()
    {
        return postedBy;
    }

    public String getTimestamp()
    {
        // TODO: format
        String formateed = "timestamp";

        try
        {
            Date date = Constants.DEFAULT_SIMPLE_DATE_FORMAT.parse(this.timestamp);

            timestamp = Constants.DEFAULT_NEWS_POST_SIMPLE_DATE_FORMAT.format(date);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return timestamp;
    }
}