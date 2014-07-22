package com.nhan.whattodo.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.model.Task;
import com.nhan.whattodo.R;
import com.nhan.whattodo.data_manager.TaskListTable;
import com.nhan.whattodo.data_manager.TaskTable;
import com.nhan.whattodo.fragment.AddTaskFragment;
import com.nhan.whattodo.fragment.TaskListFragment;
import com.nhan.whattodo.receiver.MessageReceiver;
import com.nhan.whattodo.utils.DialogUtils;
import com.nhan.whattodo.utils.GoogleTaskHelper;
import com.nhan.whattodo.utils.GoogleTaskManager;
import com.nhan.whattodo.utils.L;

import java.util.ArrayList;

/**
 * Created by ivanle on 6/29/14.
 */
public class TaskActivity extends Activity {

    public static final String KEY_TASK_GROUP_ID = "key_task_group_id";
    private long taskGroupId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_activity);
        setupActionbar();

        L.e("ON CREATE");

        // Get All Task in Parent TaskList
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(KEY_TASK_GROUP_ID)) {
            taskGroupId = intent.getLongExtra(KEY_TASK_GROUP_ID, 0);
            getFragmentManager().beginTransaction().replace(R.id.taskFragmentContainer, TaskListFragment.newInstance(taskGroupId)).commit();
        } else if (intent != null && intent.hasExtra(MessageReceiver.KEY_SOURCE)) {

            L.e("Get intent info");
            String title = intent.getStringExtra(TaskTable.FIELD_TITLE);
            long dueDate = intent.getLongExtra(TaskTable.FIELD_DUE_DATE, 0);
            final int priority = intent.getIntExtra(TaskTable.FIELD_PRIORITY, 1);
            final long group = intent.getLongExtra(TaskTable.FIELD_GROUP, 1);
            String note = intent.getStringExtra(TaskTable.FIELD_NOTE);

            final Task task = new Task();
            task.setTitle(title);
            task.setDue(new DateTime(dueDate));
            task.setStatus(TaskTable.STATUS_NEED_ACTION);
            if (note != null && !note.isEmpty())
                task.setNotes(note);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String remoteParentId = TaskListTable.getTaskListRemoteIDByLocalID(TaskActivity.this, group);
                    Task remoteTask = GoogleTaskManager.insertTask(GoogleTaskHelper.getCredential(TaskActivity.this), remoteParentId, task);

                    if (remoteTask != null) {
                        remoteTask.set(TaskTable.FIELD_PRIORITY, priority);
                        remoteTask.set(TaskTable.FIELD_GROUP, group);
                        remoteTask.set(TaskTable.FIELD_REMOTE_ID, remoteTask.getId());

                        final long insertedTaskId = TaskTable.insertTask(TaskActivity.this, remoteTask);
                        TaskActivity.this.finish();
                    }
                }
            }).start();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_list_fragment_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addTask:
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.fragment_slide_in_right, R.animator.fragment_slide_out_left)
                        .replace(R.id.taskFragmentContainer, AddTaskFragment.newInstance(taskGroupId))
                        .addToBackStack("AddTaskFragment")
                        .commit();
                return true;
            case R.id.removeTask:
                return false;
        }

        return false;
    }

    public void showTaskListFragment(ArrayList<Task> tasks) {
        DialogUtils.dismissDialog(DialogUtils.DialogType.PROGRESS_DIALOG);
        if (tasks == null) return;
        getFragmentManager().beginTransaction().replace(R.id.taskFragmentContainer, TaskListFragment.newInstance(taskGroupId)).commit();
    }

    private void setupActionbar() {
        ActionBar actionBar = getActionBar();
//        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.white)));
        actionBar.setTitle(Html.fromHtml("<font color='" + getResources().getColor(R.color.cyan) + "'><b>TASK</b></font>"));
    }

    public long getTaskGroupId() {
        return taskGroupId;
    }
}