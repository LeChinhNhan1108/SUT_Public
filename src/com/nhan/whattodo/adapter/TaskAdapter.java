package com.nhan.whattodo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.model.Task;
import com.nhan.whattodo.R;
import com.nhan.whattodo.data_manager.TaskTable;
import com.nhan.whattodo.utils.Utils;

import java.util.ArrayList;

/**
 * Created by ivanle on 7/4/14.
 */
public class TaskAdapter extends ArrayAdapter<Task> {

    private int resource;
    private ArrayList<Task> data;

    public TaskAdapter(Context context, int resource, ArrayList<Task> data) {
        super(context, resource);
        this.resource = resource;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return (Long)data.get(position).get(TaskTable._ID);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null){
            v = ((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(resource,null);
        }

        TextView tvTaskTitle = (TextView) v.findViewById(R.id.tvTaskTitle);
        TextView tvTaskDue = (TextView) v.findViewById(R.id.tvTaskDue);

        tvTaskTitle.setText(data.get(position).getTitle());
        DateTime dateTime = data.get(position).getDue();

        if (dateTime == null){
            tvTaskDue.setText("Infinity");
        }else{
            tvTaskDue.setText("Due "+Utils.convertDateToString(dateTime.getValue()));
        }
        return v;
    }
}
