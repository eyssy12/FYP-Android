package com.eyssyapps.fypcms.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eyssyapps.fypcms.R;
import com.eyssyapps.fypcms.models.Module;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by eyssy on 07/04/2016.
 */
public class ModulesRecyclerViewAdapter extends RecyclerView.Adapter<ModulesRecyclerViewAdapter.ModuleItemViewHolder>
{
    private Context context;
    private LayoutInflater inflater;
    private LinkedList<Module> items;
    private final View parentView;

    private int selectedPosition = -1;

    private Object mutex;

    public ModulesRecyclerViewAdapter(Context context, View parentView)
    {
        this.context = context;
        this.items = new LinkedList<>();
        this.inflater = LayoutInflater.from(context);
        this.parentView = parentView;

        this.mutex = new Object();
    }

    // should be added to a base class for all the adapters.. a lot of repeating code
    public boolean addAtPosition(Module item, int position)
    {
        if (items.contains(item) || position > items.size())
        {
            return false;
        }

        items.add(position, item);
        refresh();

        return true;
    }

    public boolean add(Module item)
    {
        if (items.contains(item))
        {
            return false;
        }

        items.addFirst(item);
        refresh();

        return true;
    }

    public void addNewCollection(List<Module> collection, boolean immediateRefresh)
    {
        items.clear();
        addCollection(collection, immediateRefresh);
    }

    public void replaceCollection(List<Module> newCollection, boolean immediateRefresh)
    {
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

    public void addCollection(List<Module> collection, boolean immediateRefresh)
    {
        synchronized (mutex)
        {
            if (!collection.isEmpty())
            {
                for (Module module : collection)
                {
                    items.addFirst(module);
                }
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
    public ModuleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.recycler_view_item_module, parent, false);

        return new ModuleItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ModuleItemViewHolder holder, final int position)
    {
        final Module item = items.get(position);

        holder.nameText.setText(item.getName());
        holder.typeText.setText(item.getModuleType());

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

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    class ModuleItemViewHolder extends RecyclerView.ViewHolder
    {
        TextView nameText,
            typeText;

        public ModuleItemViewHolder(View itemView)
        {
            super(itemView);

            nameText = (TextView) itemView.findViewById(R.id.module_name_text);
            typeText = (TextView) itemView.findViewById(R.id.module_type_text);
        }
    }
}