package com.nhan.whattodo.asyntask;

import android.app.Activity;
import android.os.AsyncTask;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;
import com.google.api.services.tasks.model.Tasks;
import com.nhan.whattodo.activity.TaskListActivity;
import com.nhan.whattodo.data_manager.TaskListTable;
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
            long localID = TaskListTable.insertTaskList(activity, tl);
            tl.set(TaskListTable._ID, localID);
            Tasks tasks = GoogleTaskManager.getAllTaskInTaskList(activity.getService(), tl.getId());
            if (tasks.getItems() == null || tasks.getItems().size() == 0) continue;
            for (Task task : tasks.getItems()) {
                task.set(TaskTable.FIELD_PRIORITY, TaskTable.PRIORITY.LOW.ordinal());
                task.set(TaskTable.FIELD_GROUP, localID);
                TaskTable.insertTask(activity, task);
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
        long localID = TaskListTable.insertTaskList(activity, newTaskList);
        newTaskList.set(TaskListTable._ID, localID);
        taskLists.add(newTaskList);


        newTaskList = GoogleTaskManager.insertTaskList(activity.getService(), "Work");
        localID = TaskListTable.insertTaskList(activity, newTaskList);
        newTaskList.set(TaskListTable._ID, localID);
        taskLists.add(newTaskList);

        newTaskList = GoogleTaskManager.insertTaskList(activity.getService(), "Entertainment");
        localID = TaskListTable.insertTaskList(activity, newTaskList);
        newTaskList.set(TaskListTable._ID, localID);
        taskLists.add(newTaskList);


        newTaskList = GoogleTaskManager.insertTaskList(activity.getService(), "Personal");
        localID = TaskListTable.insertTaskList(activity, newTaskList);
        newTaskList.set(TaskListTable._ID, localID);
        taskLists.add(newTaskList);

//        for (int i = 1; i <= 5; i++) {
//            newTaskList = GoogleTaskManager.insertTaskList(activity.getService(), "Testing " + i);
//            localID = TaskListTable.insertTaskList(activity, newTaskList);
//            newTaskList.set(TaskListTable._ID, localID);
//            taskLists.add(newTaskList);
//        }

        return taskLists;
    }


    @Override
    protected void onPostExecute(ArrayList<TaskList> taskLists) {
        super.onPostExecute(taskLists);
        activity.showTGFragment(taskLists);
    }
}
