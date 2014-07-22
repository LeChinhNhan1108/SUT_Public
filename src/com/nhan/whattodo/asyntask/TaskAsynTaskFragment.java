package com.nhan.whattodo.asyntask;

import android.app.Fragment;
import android.os.AsyncTask;
import com.google.api.services.tasks.model.Task;
import com.nhan.whattodo.data_manager.TaskTable;
import com.nhan.whattodo.fragment.TaskListFragment;
import com.nhan.whattodo.utils.L;

import java.util.ArrayList;

/**
 * Created by ivanle on 7/5/14.
 */
public class TaskAsynTaskFragment extends AsyncTask<Fragment, Void, ArrayList<Task>> {

    Fragment fragment;

    @Override
    protected ArrayList<Task> doInBackground(Fragment... params) {
        fragment = params[0];
        if (fragment instanceof TaskListFragment) {
            ArrayList<Task> tasks = TaskTable.getAllTaskInTaskList(fragment.getActivity(), ((TaskListFragment)fragment).getTaskGroupId());
            return tasks;
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Task> tasks) {
        super.onPostExecute(tasks);
        ((TaskListFragment)fragment).refreshListView(tasks);
    }
}
