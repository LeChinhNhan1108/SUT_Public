package com.nhan.whattodo.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.api.services.tasks.model.Task;
import com.nhan.whattodo.R;
import com.nhan.whattodo.adapter.TaskAdapter;
import com.nhan.whattodo.asyntask.TaskAsynTaskFragment;
import com.nhan.whattodo.data_manager.TaskListTable;
import com.nhan.whattodo.data_manager.TaskTable;
import com.nhan.whattodo.utils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by ivanle on 7/4/14.
 */
public class TaskListFragment extends ListFragment implements AdapterView.OnItemLongClickListener {

    private ArrayList<Task> tasks;
    private long taskGroupId;
    private TaskAdapter adapter;

    public static TaskListFragment newInstance(long taskGroupId) {
        TaskListFragment taskListFragment = new TaskListFragment();
        taskListFragment.taskGroupId = taskGroupId;
        taskListFragment.tasks = new ArrayList<Task>();
        return taskListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        getListView().setOnItemLongClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        new TaskAsynTaskFragment().execute(this);
    }

    public void refreshListView(ArrayList<Task> tasks) {
        L.e("Refresh View");
        if (tasks != null && !tasks.isEmpty()) {
            this.tasks.clear();
            this.tasks.addAll(tasks);
            adapter = new TaskAdapter(getActivity(), R.layout.task_item, this.tasks);
            setListAdapter(adapter);
        } else {
            L.t(getActivity(), "No thing to show");
            setListShown(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.removeTask:
                removeTask();
                return true;

            case R.id.updateTask:
                updateTaskStatus();
                return true;
            case R.id.sortByDueDate:
                Collections.sort(tasks, new Comparator<Task>() {
                    @Override
                    public int compare(Task lhs, Task rhs) {
                        long tempLhs = lhs.getDue() != null ? lhs.getDue().getValue() : 0;
                        long tempRhs = rhs.getDue() != null ? rhs.getDue().getValue() : 0;
                        return (int) (tempLhs - tempRhs);
                    }
                });
                L.t(getActivity(), String.format(getString(R.string.sort_task), "due date"));
                adapter.notifyDataSetChanged();
                return true;
            case R.id.sortByPriority:
                Collections.sort(tasks, new Comparator<Task>() {
                    @Override
                    public int compare(Task lhs, Task rhs) {
                        return (Integer) rhs.get(TaskTable.FIELD_PRIORITY) - (Integer) lhs.get(TaskTable.FIELD_PRIORITY);
                    }
                });
                L.t(getActivity(), String.format(getString(R.string.sort_task), "priority"));
                adapter.notifyDataSetChanged();
                return true;

        }
        return false;
    }

    private void updateTaskStatus() {
        new UpdateTaskStateAsyncTask().execute(getActivity());
    }

    private void removeTask() {
        new RemoveTaskAsyncTask().execute(getActivity());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String status = tasks.get(position).getStatus().equalsIgnoreCase(TaskTable.STATUS_COMPLETED) ? TaskTable.STATUS_NEED_ACTION : TaskTable.STATUS_COMPLETED;
        tasks.get(position).setStatus(status);
        TaskTable.updateTaskStatus(getActivity(), (Long) tasks.get(position).get(TaskTable._ID), status);
        adapter.notifyDataSetChanged();
    }

    public long getTaskGroupId() {
        return taskGroupId;
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        getActivity().getFragmentManager()
                .beginTransaction()
                .replace(R.id.taskFragmentContainer, AddTaskFragment.newInstance(tasks.get(position)))
                .addToBackStack("UpdateTaskFragment")
                .commit();
        return true;
    }

    class UpdateTaskStateAsyncTask extends AsyncTask<Activity, Void, Void> {
        @Override
        protected Void doInBackground(Activity... params) {
            String parentRemoteId = TaskListTable.getTaskListRemoteIDByLocalID(getActivity(), taskGroupId);
            boolean isConnectToIntenet = Utils.isConnectedToTheInternet(getActivity());

            if (!isConnectToIntenet) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        L.t(getActivity(), "No Internet Connection");
                    }
                });
                return null;
            }

            if (GoogleTaskHelper.getService() == null){
                GoogleTaskHelper.getCredential(getActivity());
            }

            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                L.e("Sync " + task.getTitle());
                int result = TaskTable.updateTaskStatus(getActivity(), (Long) task.get(TaskTable._ID), task.getStatus());
                if (result != 0) {
                    if (task.getId() == null || task.getId().isEmpty()) {
                        Task remoteTask = GoogleTaskManager.insertTask(GoogleTaskHelper.getService(), parentRemoteId, task);
                        if (remoteTask != null) {
                            task.set(TaskTable.FIELD_REMOTE_ID, remoteTask.getId());
                            task.setId(remoteTask.getId());
                            TaskTable.updateTask(getActivity(), task);
                        }
                    } else {
                        GoogleTaskManager.updateTask(GoogleTaskHelper.getService(), parentRemoteId, task.getId(), task);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    class RemoveTaskAsyncTask extends AsyncTask<Activity, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DialogUtils.showDialog(DialogUtils.DialogType.PROGRESS_DIALOG, getActivity(), getString(R.string.wait_for_sync));
        }

        @Override
        protected Void doInBackground(Activity... params) {
            String parentRemoteId = TaskListTable.getTaskListRemoteIDByLocalID(getActivity(), taskGroupId);
            for (int i = tasks.size() - 1; i >= 0; i--) {
                Task task = tasks.get(i);
                if (task.getStatus().equalsIgnoreCase(TaskTable.STATUS_NEED_ACTION)) continue;
                try {
                    int result = TaskTable.deleteTask(getActivity(), (Long) task.get(TaskTable._ID));
                    tasks.remove(task);
                    if (result != 0 && Utils.isConnectedToTheInternet(getActivity())) {
                        GoogleTaskManager.deleteTask(GoogleTaskHelper.getService(), parentRemoteId, task.getId());
                        Utils.cancelAlarm(getActivity(), (Integer.parseInt(task.get(TaskTable._ID) + "")));
                    }
                } catch (IOException e) {
                    L.e(e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            DialogUtils.dismissDialog(DialogUtils.DialogType.PROGRESS_DIALOG);
            adapter.notifyDataSetChanged();
            Utils.updateAppWidget(getActivity());
        }
    }

}