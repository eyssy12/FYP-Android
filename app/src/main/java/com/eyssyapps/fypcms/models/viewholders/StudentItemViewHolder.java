package com.eyssyapps.fypcms.models.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.eyssyapps.fypcms.R;

/**
 * Created by eyssy on 01/05/2016.
 */
public class StudentItemViewHolder extends RecyclerView.ViewHolder
{
    private TextView fullNameTextView;

    public StudentItemViewHolder(View itemView)
    {
        super(itemView);

        fullNameTextView = (TextView) itemView.findViewById(R.id.full_name_text);
    }

    public TextView getFullNameTextView()
    {
        return fullNameTextView;
    }
}
