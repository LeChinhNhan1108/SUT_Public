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
import com.nhan.whattodo.utils.DialogUtils;
import com.nhan.whattodo.utils.GoogleTaskHelper;
import com.nhan.whattodo.utils.GoogleTaskManager;
import com.nhan.whattodo.utils.L;

import java.io.IOException;
import java.util.ArrayList;

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
                L.t(getActivity(), "REMOVE TASK ");
                removeTask();
                return true;

            case R.id.updateTask:
                L.t(getActivity(), "UPDATE TASK ");
                updateTaskStatus();
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
    public void onPause() {
        super.onPause();
        L.t(getActivity(), " Task ListFragment Paused. Store Data");
//        new SaveTaskStateAsyncTask().execute(getActivity());

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (tasks.get(position).getStatus().equalsIgnoreCase(TaskTable.STATUS_COMPLETED))
            tasks.get(position).setStatus(TaskTable.STATUS_NEED_ACTION);
        else
            tasks.get(position).setStatus(TaskTable.STATUS_COMPLETED);

        adapter.notifyDataSetChanged();
    }

    public long getTaskGroupId() {
        return taskGroupId;
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        L.e("Long click");
        getActivity().getFragmentManager().beginTransaction().replace(R.id.taskFragmentContainer,
                AddTaskFragment.newInstance(tasks.get(position))).addToBackStack("UpdateTaskFragment").commit();
        return true;
    }

    class UpdateTaskStateAsyncTask extends AsyncTask<Activity, Void, Void> {
        @Override
        protected Void doInBackground(Activity... params) {
            String parentRemoteId = TaskListTable.getTaskListRemoteIDByLocalID(getActivity(), taskGroupId);

            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                Task result = GoogleTaskManager.updateTask(GoogleTaskHelper.getService(), parentRemoteId, task.getId(), task);
                if (result != null)
                    TaskTable.updateTaskStatus(getActivity(), (Long) task.get(TaskTable._ID), task.getStatus());
            }

            return null;
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

            for (int i = tasks.size()-1; i >= 0; i--) {
                Task task = tasks.get(i);
                if (task.getStatus().equalsIgnoreCase(TaskTable.STATUS_NEED_ACTION)) continue;
                try {
                    GoogleTaskManager.deleteTask(GoogleTaskHelper.getService(), parentRemoteId, task.getId());
                    TaskTable.deleteTask(getActivity(),(Long)task.get(TaskTable._ID));
                    tasks.remove(task);
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

        }
    }

}