package com.nhan.whattodo.asyntask;

import android.app.Activity;
import android.os.AsyncTask;
import com.google.api.services.tasks.model.Task;
import com.nhan.whattodo.activity.TaskActivity;
import com.nhan.whattodo.data_manager.TaskModel;
import com.nhan.whattodo.data_manager.TaskTable;
import com.nhan.whattodo.utils.GoogleTaskManager;
import com.nhan.whattodo.utils.L;

import java.util.ArrayList;

/**
 * Created by ivanle on 7/3/14.
 */
public class TaskAsynTask extends AsyncTask<Activity, Void, ArrayList<TaskModel>> {

    TaskActivity activity;

    @Override
    protected ArrayList<TaskModel> doInBackground(Activity... params) {
        activity = (TaskActivity) params[0];

        ArrayList<TaskModel> model = TaskTable.getAllTaskInTaskList(activity, activity.getTaskGroupId());

        Task task = new Task();
        task.setTitle("New One from AsyncTask");
        task.set("Hello", "Just addtional value");

        Task newTask = GoogleTaskManager.insertTask(activity.getService(),"MDc1MzQzNDU5NDkwOTQwNjE3MzQ6MDow", task);
        if (newTask == null){
            L.e("Insert NOT okie");
        }else{
            L.e(newTask.toString());
        }
        return model;
    }

    @Override
    protected void onPostExecute(ArrayList<TaskModel> taskModels) {
        super.onPostExecute(taskModels);
        if (taskModels == null)
            L.e("NULL");
        else
            L.e(taskModels.toString());
    }
}
