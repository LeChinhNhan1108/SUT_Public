package com.nhan.whattodo.asyntask;

import android.app.Activity;
import android.os.AsyncTask;
import com.google.api.services.tasks.model.Task;
import com.nhan.whattodo.activity.TaskActivity;
import com.nhan.whattodo.data_manager.TaskTable;
import com.nhan.whattodo.utils.GoogleTaskManager;
import com.nhan.whattodo.utils.L;

import java.util.ArrayList;

/**
 * Created by ivanle on 7/3/14.
 */
public class TaskAsynTask extends AsyncTask<Activity, Void, ArrayList<Task>> {

    TaskActivity activity;

    @Override
    protected ArrayList<Task> doInBackground(Activity... params) {
        activity = (TaskActivity) params[0];
        ArrayList<Task> tasks = TaskTable.getAllTaskInTaskList(activity, activity.getTaskGroupId());
        return tasks;
    }

    @Override
    protected void onPostExecute(ArrayList<Task> tasks) {
        super.onPostExecute(tasks);
        activity.showTaskListFragment(tasks);
    }
}
