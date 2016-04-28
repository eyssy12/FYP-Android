package com.eyssyapps.fypcms.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.eyssyapps.fypcms.utils.Constants;

import java.util.Vector;

/**
 * Created by eyssy on 08/04/2016.
 */
public class TimetablePagerAdapter extends PagerAdapter
{
    private Context context;
    private Vector<View> pages;

    public TimetablePagerAdapter(Context context, Vector<View> pages) 
    {
        this.context = context;
        this.pages = pages;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return Constants.DAYS_OF_WEEK.get(position + 1);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        View page = pages.get(position);
        container.addView(page);

        return page;
    }

    @Override
    public int getCount()
    {
        return Constants.DAYS_OF_WEEK.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((View) object);
    }
}