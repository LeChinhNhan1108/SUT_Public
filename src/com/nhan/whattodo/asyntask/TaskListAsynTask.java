package com.nhan.whattodo.asyntask;

import android.app.Activity;
import android.os.AsyncTask;
import com.google.api.services.tasks.model.TaskList;
import com.nhan.whattodo.activity.TaskListActivity;
import com.nhan.whattodo.data_manager.TaskListTable;
import com.nhan.whattodo.utils.GoogleTaskManager;

import java.util.ArrayList;

/**
 * Created by ivanle on 7/1/14.
 */
public class TaskListAsynTask extends AsyncTask<Activity, Void, ArrayList<TaskList>> {

    TaskListActivity activity;

    @Override
    protected ArrayList<TaskList> doInBackground(Activity... params) {
        activity = (TaskListActivity) params[0];
        ArrayList<TaskList> taskLists = TaskListTable.getAllTaskList(activity);
        if (taskLists == null) {
            TaskList newTaskList;

            newTaskList = GoogleTaskManager.insertTaskList(activity.getService(), "School");
            TaskListTable.insertTaskList(activity, newTaskList);


            newTaskList = GoogleTaskManager.insertTaskList(activity.getService(), "Work");
            TaskListTable.insertTaskList(activity, newTaskList);

            newTaskList = GoogleTaskManager.insertTaskList(activity.getService(), "Entertainment");
            TaskListTable.insertTaskList(activity, newTaskList);

            newTaskList = GoogleTaskManager.insertTaskList(activity.getService(), "Personal");
            TaskListTable.insertTaskList(activity, newTaskList);

        }
        return taskLists;
    }


    @Override
    protected void onPostExecute(ArrayList<TaskList> taskLists) {
        super.onPostExecute(taskLists);
        activity.showTGFragment(taskLists);
    }
}
