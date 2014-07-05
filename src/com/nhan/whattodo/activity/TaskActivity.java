package com.nhan.whattodo.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.nhan.whattodo.R;
import com.nhan.whattodo.asyntask.TaskAsynTask;
import com.nhan.whattodo.fragment.AddTaskFragment;
import com.nhan.whattodo.fragment.TaskListFragment;
import com.nhan.whattodo.utils.DialogUtils;
import com.nhan.whattodo.utils.GoogleTaskHelper;
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

        // Get All Task in Parent TaskList
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(KEY_TASK_GROUP_ID)){
            taskGroupId = intent.getLongExtra(KEY_TASK_GROUP_ID, 0);
            getFragmentManager().beginTransaction().replace(R.id.taskFragmentContainer, TaskListFragment.newInstance(taskGroupId)).commit();
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
                L.e("task group id  TASK LIST AC " + taskGroupId);
                getFragmentManager().beginTransaction().replace(R.id.taskFragmentContainer, AddTaskFragment.newInstance(taskGroupId)).addToBackStack("AddTaskFragment").commit();
                return true;
        }

        return false;
    }

    public void showTaskListFragment(ArrayList<Task> tasks){
        DialogUtils.dismissDialog(DialogUtils.DialogType.PROGRESS_DIALOG);
        if (tasks == null) return;
        getFragmentManager().beginTransaction().replace(R.id.taskFragmentContainer, TaskListFragment.newInstance(taskGroupId)).commit();
    }

    private void setupActionbar() {
        ActionBar actionBar= getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.white)));
        actionBar.setTitle(Html.fromHtml("<font color='" + getResources().getColor(R.color.cyan) + "'><b>TASK</b></font>"));
    }

    public long getTaskGroupId() {
        return taskGroupId;
    }

}