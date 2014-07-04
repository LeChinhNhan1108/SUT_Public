package com.nhan.whattodo.asyntask;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.SystemClock;
import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;
import com.google.api.services.tasks.model.Tasks;
import com.nhan.whattodo.activity.TaskListActivity;
import com.nhan.whattodo.data_manager.TaskListTable;
import com.nhan.whattodo.data_manager.TaskModel;
import com.nhan.whattodo.data_manager.TaskTable;
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
            taskLists = syncTaskListFromServer();
            saveDataFromServerToDB(taskLists);
            // If only default list exist, create 4 new task groups
            if (taskLists.size() == 1){
                taskLists.addAll(createNewTaskList());
            }
        }
        return taskLists;
    }


    public void saveDataFromServerToDB(ArrayList<TaskList> taskLists){
        for (TaskList tl : taskLists) {
            long parentId = TaskListTable.insertTaskList(activity, tl);
            tl.set(TaskListTable._ID, parentId);
            Tasks tasks = GoogleTaskManager.getAllTaskInTaskList(activity.getService(), tl.getId());
            if (tasks.getItems() == null || tasks.getItems().size() == 0) continue;
            for (Task task : tasks.getItems()) {
                TaskModel model = new TaskModel(TaskModel.PRIORITY.MEDIUM.ordinal(), parentId, task);
                TaskTable.insertTask(activity, model);
            }
        }
    }

    private ArrayList<TaskList> syncTaskListFromServer() {
        TaskLists taskLists = GoogleTaskManager.getAllTaskList(activity.getService());
        return (ArrayList<TaskList>) taskLists.getItems();
    }

    private ArrayList<TaskList> createNewTaskList() {
        ArrayList<TaskList> taskLists = new ArrayList<TaskList>();
        TaskList newTaskList;

        newTaskList = GoogleTaskManager.insertTaskList(activity.getService(), "School");
        long parentId = TaskListTable.insertTaskList(activity, newTaskList);
        newTaskList.set(TaskListTable._ID, parentId);
        taskLists.add(newTaskList);


        newTaskList = GoogleTaskManager.insertTaskList(activity.getService(), "Work");
        parentId = TaskListTable.insertTaskList(activity, newTaskList);
        newTaskList.set(TaskListTable._ID, parentId);
        taskLists.add(newTaskList);

        newTaskList = GoogleTaskManager.insertTaskList(activity.getService(), "Entertainment");
        parentId = TaskListTable.insertTaskList(activity, newTaskList);
        newTaskList.set(TaskListTable._ID, parentId);
        taskLists.add(newTaskList);


        newTaskList = GoogleTaskManager.insertTaskList(activity.getService(), "Personal");
        parentId = TaskListTable.insertTaskList(activity, newTaskList);
        newTaskList.set(TaskListTable._ID, parentId);
        taskLists.add(newTaskList);
        return taskLists;
    }


    @Override
    protected void onPostExecute(ArrayList<TaskList> taskLists) {
        super.onPostExecute(taskLists);
        activity.showTGFragment(taskLists);
    }
}