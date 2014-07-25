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
import com.nhan.whattodo.utils.L;
import com.nhan.whattodo.utils.Utils;

import java.util.ArrayList;

/**
 * Created by ivanle on 7/1/14.
 */
public class TaskListAsynTask extends AsyncTask<Activity, Void, ArrayList<TaskList>> {

    TaskListActivity activity;

    private String SCHOOL_TASK_GROUP = "School";
    private String WORK_TASK_GROUP = "Work";
    private String ENTERTAINMENT_TASK_GROUP = "Entertainment";
    private String PERSONAL_TASK_GROUP = "Personal";

    @Override
    protected ArrayList<TaskList> doInBackground(Activity... params) {
        activity = (TaskListActivity) params[0];

        ArrayList<TaskList> taskLists = TaskListTable.getAllTaskList(activity);

        // Database is empty, create 4 predefined Task groups
        if (taskLists == null) {
            taskLists = new ArrayList<TaskList>();
            TaskList schoolTask = createNewTaskList(SCHOOL_TASK_GROUP);
            TaskList workTask = createNewTaskList(WORK_TASK_GROUP);
            TaskList entertainmentTask = createNewTaskList(ENTERTAINMENT_TASK_GROUP);
            TaskList personalTask = createNewTaskList(PERSONAL_TASK_GROUP);

            taskLists.add(schoolTask);
            taskLists.add(workTask);
            taskLists.add(entertainmentTask);
            taskLists.add(personalTask);

            if (Utils.isConnectedToTheInternet(activity)){
                // Sync task groups from server and save to database
                ArrayList<TaskList> serverTaskList =  syncTaskListFromServer();
                saveDataFromServerToDB(serverTaskList);

                // Sync All TaskList to server
                TaskList taskListFromServer = null;

                taskListFromServer = GoogleTaskManager.insertTaskList(activity.getService(),SCHOOL_TASK_GROUP);
                TaskListTable.updateRemoteIDByLocalID(activity,schoolTask,taskListFromServer.getId());

                taskListFromServer = GoogleTaskManager.insertTaskList(activity.getService(),WORK_TASK_GROUP);
                TaskListTable.updateRemoteIDByLocalID(activity,workTask,taskListFromServer.getId());

                taskListFromServer = GoogleTaskManager.insertTaskList(activity.getService(),ENTERTAINMENT_TASK_GROUP);
                TaskListTable.updateRemoteIDByLocalID(activity,entertainmentTask,taskListFromServer.getId());

                taskListFromServer = GoogleTaskManager.insertTaskList(activity.getService(),PERSONAL_TASK_GROUP);
                TaskListTable.updateRemoteIDByLocalID(activity,personalTask,taskListFromServer.getId());

                taskLists.addAll(serverTaskList);
            }
        }
        return taskLists;
    }

    public void saveDataFromServerToDB(ArrayList<TaskList> taskLists) {
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

    private TaskList createNewTaskList(String title) {
        TaskList newTaskList;

        newTaskList = new TaskList();
        newTaskList.setTitle(title);
        long id = TaskListTable.insertTaskList(activity, newTaskList);
        newTaskList.set(TaskListTable._ID, id);

        return newTaskList;
    }

    @Override
    protected void onPostExecute(ArrayList<TaskList> taskLists) {
        super.onPostExecute(taskLists);
        activity.showTGFragment(taskLists);
    }
}
