package com.nhan.whattodo.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
        return (Long) data.get(position).get(TaskTable._ID);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(resource, null);

        TextView tvTaskTitle = (TextView) v.findViewById(R.id.tvTaskTitle);
        TextView tvTaskDue = (TextView) v.findViewById(R.id.tvTaskDue);
        CheckBox cbCompleted = (CheckBox) v.findViewById(R.id.cbxComplete);

        boolean completed = data.get(position).getStatus() != null && data.get(position).getStatus().equalsIgnoreCase(TaskTable.STATUS_COMPLETED);
        cbCompleted.setChecked(completed);

        if (completed) {
            tvTaskTitle.setPaintFlags(tvTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            tvTaskTitle.setPaintFlags(tvTaskTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        tvTaskTitle.setText(data.get(position).getTitle());
        DateTime dateTime = data.get(position).getDue();

        if (dateTime == null) {
            tvTaskDue.setText("Infinity");
        } else {
            tvTaskDue.setText("Due " + Utils.convertDateToString(dateTime.getValue()));
        }

        cbCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                data.get(position).setStatus(isChecked ? TaskTable.STATUS_COMPLETED : TaskTable.STATUS_NEED_ACTION);
                notifyDataSetChanged();
            }
        });
        return v;
    }
}
