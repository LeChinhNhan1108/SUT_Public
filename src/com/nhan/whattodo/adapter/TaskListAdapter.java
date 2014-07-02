package com.nhan.whattodo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.google.api.services.tasks.model.TaskList;
import com.nhan.whattodo.R;
import com.nhan.whattodo.utils.L;

import java.util.ArrayList;

/**
 * Created by ivanle on 7/2/14.
 */
public class TaskListAdapter extends ArrayAdapter<TaskList> {

    private int resource;
    private ArrayList<TaskList> data;

    public TaskListAdapter(Context context, int resource, ArrayList<TaskList> data) {
        super(context, resource);
        this.resource = resource;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(resource, null);
        }
        TextView tvTitle = (TextView) v.findViewById(R.id.taskListTitle);
        tvTitle.setText(data.get(position).getTitle());
        L.d("data " + data.get(position).getTitle());
        return v;
    }


}
