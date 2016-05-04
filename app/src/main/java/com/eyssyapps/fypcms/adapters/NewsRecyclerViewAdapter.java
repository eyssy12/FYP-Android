package com.eyssyapps.fypcms.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.eyssyapps.fypcms.R;
import com.eyssyapps.fypcms.activities.common.ReadNewsPostActivity;
import com.eyssyapps.fypcms.models.NewsPost;
import com.eyssyapps.fypcms.models.viewholders.NewsItemViewHolder;

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

        // TODO: figure out how to handle youtube/image/media clip arts
        holder.getTitleText().setText(item.getTitle());
        holder.getPostedByText().setText(item.getPostedBy());
        holder.getTimestampText().setText(item.getTimestamp());

        holder.getReadMore().setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;
                        //overlay is black with transparency of 0x77 (119)
                        view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate();

                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        //clear the overlay
                        view.getDrawable().clearColorFilter();
                        view.invalidate();

                        Intent intent = new Intent(context, ReadNewsPostActivity.class);
                        intent.putExtra(ReadNewsPostActivity.WEBVIEW_CONTENT, item.getBody());

                        context.startActivity(intent);

                        break;
                    }
                }

                return true;
            }
        });
    }
}