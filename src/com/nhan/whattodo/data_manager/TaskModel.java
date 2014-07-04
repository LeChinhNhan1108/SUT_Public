package com.nhan.whattodo.data_manager;

import com.google.api.services.tasks.model.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivanle on 7/3/14.
 */
public class TaskModel{

    private int priority;
    private long group_id;
    private List<Integer> collaborators;
    private Task task;

    public TaskModel(int priority, long group_id, Task task) {
        this.priority = priority;
        this.group_id = group_id;
        this.task = task;
        collaborators = new ArrayList<Integer>();
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public List<Integer> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(List<Integer> collaborators) {
        this.collaborators = collaborators;
    }

    public long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(long group_id) {
        this.group_id = group_id;
    }

    @Override
    public String toString() {
        return "G " + group_id + " PR "+ priority + task.toString();
    }

    public enum PRIORITY{
        HIGH, MEDIUM, LOW
    }
}
