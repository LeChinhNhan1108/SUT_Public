package com.nhan.whattodo.asyntask;

import android.app.Activity;
import android.os.AsyncTask;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.nhan.whattodo.activity.TaskListActivity;
import com.nhan.whattodo.data_manager.TaskListTable;
import com.nhan.whattodo.data_manager.TaskTable;
import com.nhan.whattodo.utils.GoogleTaskHelper;
import com.nhan.whattodo.utils.GoogleTaskManager;
import com.nhan.whattodo.utils.L;
import com.nhan.whattodo.utils.Utils;

import java.util.ArrayList;

/**
 * Created by ivanle on 7/3/14.
 */
public class SyncAllDBAsyncTask extends AsyncTask<Activity, Void, Boolean> {
    TaskListActivity activity;
    @Override
    protected Boolean doInBackground(Activity... params) {
        L.e("Sync task to server");
        activity = (TaskListActivity) params[0];
        if (!Utils.isConnectedToTheInternet(activity)) return false;

        ArrayList<TaskList> taskLists = TaskListTable.getAllTaskList(activity);
        if (taskLists != null && !taskLists.isEmpty()) {
            for (TaskList taskList : taskLists) {
                ArrayList<Task> tasks = TaskTable.getAllTaskInTaskList(activity, (Long) taskList.get(TaskListTable._ID));
                if (tasks == null) continue;
                for (Task task : tasks) {
                    if (task.getId() == null || task.getId().isEmpty()) {
                        Task remoteTask = GoogleTaskManager.insertTask(GoogleTaskHelper.getService(), taskList.getId(), task);
                        // Add remote id to local
                        if (remoteTask != null) {
                            task.set(TaskTable.FIELD_REMOTE_ID, remoteTask.getId());
                            TaskTable.updateTask(activity, task);
                        }
                    } else {
                        if (GoogleTaskHelper.getService() == null)
                            GoogleTaskHelper.getCredential(activity);
                        GoogleTaskManager.updateTask(GoogleTaskHelper.getService(), taskList.getId(), task.getId(), task);
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result)
            L.t(activity," Task Sync Successfully");
        else
            L.t(activity," Task Sync Failed");
    }
}
