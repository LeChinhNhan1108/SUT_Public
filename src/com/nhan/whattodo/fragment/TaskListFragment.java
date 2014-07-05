package com.nhan.whattodo.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.*;
import android.widget.ListView;
import com.google.api.services.tasks.model.Task;
import com.nhan.whattodo.R;
import com.nhan.whattodo.adapter.TaskAdapter;
import com.nhan.whattodo.utils.L;

import java.util.ArrayList;

/**
 * Created by ivanle on 7/4/14.
 */
public class TaskListFragment extends ListFragment {

    private ArrayList<Task> tasks;

    public static TaskListFragment newInstance(ArrayList<Task> tasks) {
        TaskListFragment taskListFragment = new TaskListFragment();
        taskListFragment.tasks = tasks;
        L.e("Task Fragment " + tasks);
        return taskListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TaskAdapter adapter = new TaskAdapter(getActivity(), R.layout.task_item, tasks);
        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }



    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        L.t(getActivity(), "Id " + id);

    }
}