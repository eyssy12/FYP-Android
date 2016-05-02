package com.eyssyapps.fypcms.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;

import com.eyssyapps.fypcms.R;
import com.eyssyapps.fypcms.models.StudentPerson;
import com.eyssyapps.fypcms.models.viewholders.StudentItemViewHolder;

/**
 * Created by eyssy on 07/04/2016.
 */
public class ClassmatesRecyclerViewAdapter extends RecyclerViewAdapterBase<StudentPerson, StudentItemViewHolder>
{
    public ClassmatesRecyclerViewAdapter(Context context, View parentView)
    {
        super(context, parentView);
    }

    @Override
    public StudentItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.recycler_view_item_classmate, parent, false);

        return new StudentItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StudentItemViewHolder holder, final int position)
    {
        final StudentPerson item = items.get(position);

        holder.getFullNameTextView().setText(item.getPerson().getFullName());

        if (selectedPosition == position)
        {
            int selectedColor = ContextCompat.getColor(context, R.color.colorPrimaryViewSelection);

            // Here I am just highlighting the background
            holder.itemView.setBackgroundColor(selectedColor);
        }
        else
        {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (position == selectedPosition)
                {
                    selectedPosition = -1;
                    notifyItemChanged(selectedPosition);
                }
                else
                {
                    notifyItemChanged(selectedPosition);
                    selectedPosition = position;
                    notifyItemChanged(selectedPosition);
                }
            }
        });
    }
}
