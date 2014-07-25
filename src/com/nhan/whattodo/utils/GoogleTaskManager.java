package com.nhan.whattodo.utils;

import android.app.Activity;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;

import java.io.IOException;

/**
 * Created by ivanle on 7/2/14.
 */
public class GoogleTaskManager {

    /* Task List Manager */

    public static TaskLists getAllTaskList(Tasks service) {
        try {
            return service.tasklists().list().execute();
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static TaskList insertTaskList(Tasks service, String title) {
        try {
            TaskList taskList = new TaskList();
            taskList.setTitle(title);
            return service.tasklists().insert(taskList).execute();
        } catch (IOException e) {
            L.e(e.getMessage());
        }
        return null;
    }

    public static void clearAllTaskList(Tasks service) {
        L.i("Clear All TL");
        TaskLists temp = getAllTaskList(service);
        for (int i = 0; i < temp.getItems().size(); i++) {
            try {
                L.d("Task List " + temp.getItems().get(i).toString());
                service.tasklists().delete(temp.getItems().get(i).getId()).execute();
            } catch (IOException e) {
                L.e("Default Task Cannot Be Deleted");
                com.google.api.services.tasks.model.Tasks tasks = getAllTaskInTaskList(service, temp.getItems().get(i).getId());
                for (int j = 0; j < tasks.getItems().size(); j++) {
                    try {
                        service.tasks().delete(temp.getItems().get(i).getId(), tasks.getItems().get(j).getId()).execute();
                    } catch (IOException e1) {
                        L.e("Task in Default cannot be deleted");

                    }
                }
                L.e("Clear all task in Default");
            }
        }
    }


    /* Task Manager */
    public static com.google.api.services.tasks.model.Tasks getAllTaskInTaskList(Tasks service, String taskListID) {
        try {
            return service.tasks().list(taskListID).execute();
        } catch (IOException e) {
            L.e(e.getMessage());
        }
        return null;
    }

    public static Task insertTask(Tasks service, String parentId, Task task) {
        try {
            return service.tasks().insert(parentId, task).execute();
        } catch (IOException e) {
            L.e(e.getMessage());
        }
        return null;
    }

    public static Task updateTask(Tasks service, String taskListId, String taskId, Task task) {
        try {
            return service.tasks().update(taskListId, taskId, task).execute();
        } catch (IOException e) {
            L.e(e.getMessage());
        }
        return null;
    }

    public static void deleteTask(Tasks service, String taskListId, String taskId) throws IOException {
        service.tasks().delete(taskListId, taskId).execute();
    }
}
