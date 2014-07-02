package com.nhan.whattodo.utils;

import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;

import java.io.IOException;

/**
 * Created by ivanle on 7/2/14.
 */
public class GoogleTaskManager {

    public static TaskLists getAllTaskList(Tasks service) {
        try {
            return service.tasklists().list().execute();
        } catch (IOException e) {
            L.e(e.getMessage());
        }
        return null;
    }

    public static TaskList insertTaskList(Tasks service, String title){
        try {
            TaskList taskList = new TaskList();
            taskList.setTitle(title);
            return service.tasklists().insert(taskList).execute();
        } catch (IOException e) {
            L.e(e.getMessage());
        }
        return null;
    }
}
