package com.eyssyapps.fypcms.models.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.eyssyapps.fypcms.R;

/**
 * Created by eyssy on 01/05/2016.
 */
public class ModuleItemViewHolder extends RecyclerView.ViewHolder
{
    private TextView nameText,
        typeText;

    public ModuleItemViewHolder(View itemView)
    {
        super(itemView);

        nameText = (TextView) itemView.findViewById(R.id.module_name_text);
        typeText = (TextView) itemView.findViewById(R.id.module_type_text);
    }

    public TextView getNameText()
    {
        return nameText;
    }

    public TextView getTypeText()
    {
        return typeText;
    }
}
