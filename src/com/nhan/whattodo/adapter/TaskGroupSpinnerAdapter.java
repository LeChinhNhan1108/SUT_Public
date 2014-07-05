package com.nhan.whattodo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.google.api.services.tasks.model.TaskList;
import com.nhan.whattodo.R;
import com.nhan.whattodo.data_manager.TaskListTable;
import com.nhan.whattodo.utils.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivanle on 7/4/14.
 */
public class TaskGroupSpinnerAdapter extends ArrayAdapter<TaskList> {


    private int resource;
    private ArrayList<TaskList> data;

    public TaskGroupSpinnerAdapter(Context context, int resource, ArrayList<TaskList> data) {
        super(context, resource);
        this.resource = resource;
        this.data = data;
    }


}
