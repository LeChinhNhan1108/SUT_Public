package com.nhan.whattodo.asyntask;

import android.app.Activity;
import android.os.AsyncTask;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;
import com.nhan.whattodo.activity.TaskGroupActivity;
import com.nhan.whattodo.utils.DialogUtils;
import com.nhan.whattodo.utils.L;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ivanle on 7/1/14.
 */
public class TaskGroupAsynTask extends AsyncTask<Activity,Void,TaskLists> {

    TaskGroupActivity activity;

    @Override
    protected TaskLists doInBackground(Activity... params) {
        activity = (TaskGroupActivity) params[0];
        Tasks service = activity.getService();
        L.d("Execute");
        TaskLists taskLists = null;
        try {
           taskLists = service.tasklists().list().execute();
        } catch (IOException e) {
            L.d(e.getMessage());
        }

        return taskLists;
    }
    @Override
    protected void onPostExecute(TaskLists taskLists) {
        super.onPostExecute(taskLists);
        ArrayList<String> tasksTitle = new ArrayList<String>();
        for (int i = 0; i < taskLists.getItems().size(); i++){
            tasksTitle.add(taskLists.getItems().get(i).getTitle());
        }
        activity.showTGFragment(tasksTitle);
    }
}
