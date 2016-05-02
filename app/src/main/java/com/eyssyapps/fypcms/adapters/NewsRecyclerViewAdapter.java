package com.eyssyapps.fypcms.adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;

import com.eyssyapps.fypcms.R;
import com.eyssyapps.fypcms.models.NewsPost;
import com.eyssyapps.fypcms.models.viewholders.NewsItemViewHolder;
import com.eyssyapps.fypcms.utils.view.SystemMessagingUtils;

/**
 * Created by eyssy on 06/04/2016.
 */
public class NewsRecyclerViewAdapter extends RecyclerViewAdapterBase<NewsPost, NewsItemViewHolder>
{
    public NewsRecyclerViewAdapter(Context context, View parentView)
    {
        super(context, parentView);
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
}