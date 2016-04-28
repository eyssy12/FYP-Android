package com.eyssyapps.fypcms.adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eyssyapps.fypcms.R;
import com.eyssyapps.fypcms.models.NewsPost;
import com.eyssyapps.fypcms.models.viewholders.NewsItemViewHolder;
import com.eyssyapps.fypcms.utils.view.SystemMessagingUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by eyssy on 06/04/2016.
 */
public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsItemViewHolder>
{
    // TODO: separate out this common code for all the adapters into a base class
    private Context context;
    private LayoutInflater inflater;
    private LinkedList<NewsPost> items;
    private final View parentView;

    private Object mutex;

    public NewsRecyclerViewAdapter(Context context, View parentView)
    {
        this.context = context;
        this.items = new LinkedList<>();
        this.inflater = LayoutInflater.from(context);
        this.parentView = parentView;

        this.mutex = new Object();
    }

    public boolean addAtPosition(NewsPost item, int position)
    {
        if (items.contains(item) || position > items.size())
        {
            return false;
        }

        items.add(position, item);
        refresh();

        return true;
    }

    public boolean add(NewsPost item)
    {
        if (items.contains(item))
        {
            return false;
        }

        items.addFirst(item);
        refresh();

        return true;
    }

    public void addNewCollection(List<NewsPost> collection, boolean immediateRefresh)
    {
        items.clear();
        addCollection(collection, immediateRefresh);
    }

    public void addCollection(List<NewsPost> collection, boolean immediateRefresh)
    {
        synchronized (mutex)
        {
            for (NewsPost newsPost : collection)
            {
                items.addFirst(newsPost);
            }
        }

        if (immediateRefresh)
        {
            refresh();
        }
    }

    public void replaceCollection(List<NewsPost> newCollection, boolean immediateRefresh)
    {
        // TODO: might need to order this by timestamp
        synchronized (mutex)
        {
            if (!newCollection.isEmpty())
            {
                items = new LinkedList<>(newCollection);
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
    public NewsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.recycler_view_item_newspost, parent, false);

        return new NewsItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsItemViewHolder holder, int position)
    {
        final NewsPost item = items.get(position);

        // TODO: should only display max 6-10 lines, give an onclick listener to expand the text or to show a dialog with the full text

        // TODO: figure out how to handle youtube/image/media clip arts
//        URLImageParser parser = new URLImageParser(holder.bodyText, context);
//        Spanned htmlText = Html.fromHtml(item.getBody(), parser, null);

//        Spanned spanned = Html.fromHtml(item.getBody(), null, new MyTagHandler());
//
//        holder.getBodyText().setText(spanned);
        holder.getTitleText().setText(item.getTitle());
        holder.getPostedByText().setText(item.getPostedBy());
        holder.getTimestampText().setText(item.getTimestamp());
        holder.getWebView().loadDataWithBaseURL("", item.getBody(), "text/html", "UTF-8", "");

        holder.getWebView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SystemMessagingUtils.showSnackBar(parentView, "webview", Snackbar.LENGTH_SHORT);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }
}
