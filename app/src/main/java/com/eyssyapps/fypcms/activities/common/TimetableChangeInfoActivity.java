package com.eyssyapps.fypcms.activities.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.eyssyapps.fypcms.R;

import butterknife.ButterKnife;

public class TimetableChangeInfoActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_change_info);

        ButterKnife.bind(this);
    }
}