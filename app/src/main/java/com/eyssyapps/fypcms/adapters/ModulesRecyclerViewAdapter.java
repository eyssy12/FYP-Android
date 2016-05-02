package com.eyssyapps.fypcms.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;

import com.eyssyapps.fypcms.R;
import com.eyssyapps.fypcms.models.Module;
import com.eyssyapps.fypcms.models.viewholders.ModuleItemViewHolder;

/**
 * Created by eyssy on 07/04/2016.
 */
public class ModulesRecyclerViewAdapter extends RecyclerViewAdapterBase<Module, ModuleItemViewHolder>
{
    public ModulesRecyclerViewAdapter(Context context, View parentView)
    {
        super(context, parentView);
    }

    @Override
    public ModuleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.recycler_view_item_module, parent, false);

        return new ModuleItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ModuleItemViewHolder holder, final int position)
    {
        final Module item = items.get(position);

        holder.getNameText().setText(item.getName());
        holder.getTypeText().setText(item.getModuleType());

        if (selectedPosition == position)
        {
            // Here I am just highlighting the background
            int selectedColor = ContextCompat.getColor(context, R.color.colorPrimaryViewSelection);
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

//                // Updating old as well as new positions
//                notifyItemChanged(selectedPosition);
//                selectedPosition = position;
//                notifyItemChanged(selectedPosition);
            }
        });
    }
}