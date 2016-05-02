package com.eyssyapps.fypcms.custom;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.eyssyapps.fypcms.R;
import com.eyssyapps.fypcms.adapters.EventRecyclerViewAdapter;
import com.eyssyapps.fypcms.adapters.TimetablePagerAdapter;
import com.eyssyapps.fypcms.enumerations.TimetableType;
import com.eyssyapps.fypcms.utils.view.ViewUtils;

import java.util.Vector;

/**
 * Created by eyssy on 28/04/2016.
 */
public class TabbedWeekViewPager
{
    private final Context context;
    private final View parentView;
    private LayoutInflater inflater;

    private EventRecyclerViewAdapter mondayAdapter;
    private EventRecyclerViewAdapter tuesdayAdapter;
    private EventRecyclerViewAdapter wednesdayAdapter;
    private EventRecyclerViewAdapter thursdayAdapter;
    private EventRecyclerViewAdapter fridayAdapter;
    private ViewPager viewPager;

    public TabbedWeekViewPager(Context context, View parentView, TimetableType timetableType)
    {
        this.context = context;
        this.parentView = parentView;
        this.inflater = LayoutInflater.from(context);

        initialise(timetableType);
    }

    private void initialise(TimetableType timetableType)
    {
        Vector<View> pages = new Vector<>();

        Pair<EmptyRecyclerView, EventRecyclerViewAdapter> pair1 = ViewUtils.createRecyclerViewAdapterPair(context, inflater, timetableType);
        Pair<EmptyRecyclerView, EventRecyclerViewAdapter> pair2 = ViewUtils.createRecyclerViewAdapterPair(context, inflater, timetableType);
        Pair<EmptyRecyclerView, EventRecyclerViewAdapter> pair3 = ViewUtils.createRecyclerViewAdapterPair(context, inflater, timetableType);
        Pair<EmptyRecyclerView, EventRecyclerViewAdapter> pair4 = ViewUtils.createRecyclerViewAdapterPair(context, inflater, timetableType);
        Pair<EmptyRecyclerView, EventRecyclerViewAdapter> pair5 = ViewUtils.createRecyclerViewAdapterPair(context, inflater, timetableType);

        pages.add(pair1.first);
        pages.add(pair2.first);
        pages.add(pair3.first);
        pages.add(pair4.first);
        pages.add(pair5.first);

        mondayAdapter = pair1.second;
        tuesdayAdapter = pair2.second;
        wednesdayAdapter = pair3.second;
        thursdayAdapter = pair4.second;
        fridayAdapter = pair5.second;

        viewPager = (ViewPager) parentView.findViewById(R.id.container);
        viewPager.setAdapter(new TimetablePagerAdapter(context, pages));

        TabLayout tabLayout = (TabLayout) parentView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public ViewPager getViewPager()
    {
        return viewPager;
    }

    public EventRecyclerViewAdapter getMondayAdapter()
    {
        return mondayAdapter;
    }

    public EventRecyclerViewAdapter getTuesdayAdapter()
    {
        return tuesdayAdapter;
    }

    public EventRecyclerViewAdapter getWednesdayAdapter()
    {
        return wednesdayAdapter;
    }

    public EventRecyclerViewAdapter getThursdayAdapter()
    {
        return thursdayAdapter;
    }

    public EventRecyclerViewAdapter getFridayAdapter()
    {
        return fridayAdapter;
    }
}
