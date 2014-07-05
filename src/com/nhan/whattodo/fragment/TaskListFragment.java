package com.nhan.whattodo.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.api.services.tasks.model.Task;
import com.nhan.whattodo.R;
import com.nhan.whattodo.adapter.TaskAdapter;
import com.nhan.whattodo.asyntask.TaskAsynTaskFragment;
import com.nhan.whattodo.data_manager.TaskTable;
import com.nhan.whattodo.utils.L;

import java.util.ArrayList;

/**
 * Created by ivanle on 7/4/14.
 */
public class TaskListFragment extends ListFragment implements AdapterView.OnItemLongClickListener {

    private ArrayList<Task> tasks;
    private long taskGroupId;

    public static TaskListFragment newInstance(long taskGroupId) {
        TaskListFragment taskListFragment = new TaskListFragment();
        taskListFragment.taskGroupId = taskGroupId;
        taskListFragment.tasks = new ArrayList<Task>();
        return taskListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        getListView().setOnItemLongClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        new TaskAsynTaskFragment().execute(this);
    }

    public void refreshListView(ArrayList<Task> tasks) {
        L.e("Refresh View");
        if (tasks != null && !tasks.isEmpty()){
            this.tasks.clear();
            this.tasks.addAll(tasks);
            TaskAdapter adapter = new TaskAdapter(getActivity(), R.layout.task_item, this.tasks);
            setListAdapter(adapter);
        }else{
            L.t(getActivity(), "No thing to show");
            setListShown(true);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        L.t(getActivity(), "Id " + id + " -- " + tasks.get(position).get(TaskTable._ID));

    }


    public long getTaskGroupId() {
        return taskGroupId;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        L.e("Long click");
//        getActivity().getFragmentManager().beginTransaction().replace(R.id.taskFragmentContainer, )
        return true;
    }
}