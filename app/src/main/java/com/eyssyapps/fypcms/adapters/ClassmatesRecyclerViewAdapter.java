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
import com.eyssyapps.fypcms.models.StudentPerson;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by eyssy on 07/04/2016.
 */
public class ClassmatesRecyclerViewAdapter extends RecyclerView.Adapter<ClassmatesRecyclerViewAdapter.StudentItemViewHolder>
{
    private Context context;
    private LayoutInflater inflater;
    private LinkedList<StudentPerson> items;
    private final View parentView;

    private int selectedPosition = -1;
    private Object mutex;

    public ClassmatesRecyclerViewAdapter(Context context, View parentView)
    {
        this.context = context;
        this.items = new LinkedList<>();
        this.inflater = LayoutInflater.from(context);
        this.parentView = parentView;

        this.mutex = new Object();
    }

    public boolean addAtPosition(StudentPerson item, int position)
    {
        if (items.contains(item) || position > items.size())
        {
            return false;
        }

        items.add(position, item);
        refresh();

        return true;
    }

    public boolean add(StudentPerson item)
    {
        if (items.contains(item))
        {
            return false;
        }

        items.addFirst(item);
        refresh();

        return true;
    }

    public void addNewCollection(List<StudentPerson> collection, boolean immediateRefresh)
    {
        items.clear();
        addCollection(collection, immediateRefresh);
    }

    public void replaceCollection(List<StudentPerson> newCollection, boolean immediateRefresh)
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

    public void addCollection(List<StudentPerson> collection, boolean immediateRefresh)
    {
        synchronized (mutex)
        {
            if (!collection.isEmpty())
            {
                for (StudentPerson classmate : collection)
                {
                    items.addFirst(classmate);
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
    public StudentItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.recycler_view_item_classmate, parent, false);

        return new StudentItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StudentItemViewHolder holder, final int position)
    {
        final StudentPerson item = items.get(position);

        holder.fullNameTextView.setText(item.getPerson().getFullName());

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

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    class StudentItemViewHolder extends RecyclerView.ViewHolder
    {
        TextView fullNameTextView;

        public StudentItemViewHolder(View itemView)
        {
            super(itemView);

            fullNameTextView = (TextView) itemView.findViewById(R.id.full_name_text);
        }
    }
}
