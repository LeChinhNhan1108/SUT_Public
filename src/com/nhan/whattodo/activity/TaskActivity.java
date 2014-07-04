package com.nhan.whattodo.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import com.google.api.services.tasks.Tasks;
import com.nhan.whattodo.R;
import com.nhan.whattodo.asyntask.TaskAsynTask;
import com.nhan.whattodo.utils.GoogleTaskHelper;
import com.nhan.whattodo.utils.L;

/**
 * Created by ivanle on 6/29/14.
 */
public class TaskActivity extends Activity {

    public static final String KEY_TASK_GROUP_ID = "key_task_group_id";

    private long taskGroupId;
    private Tasks service;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_activity);
        setupActionbar();



        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(KEY_TASK_GROUP_ID)){
            taskGroupId = intent.getLongExtra(KEY_TASK_GROUP_ID, 0);
            L.e(taskGroupId + " group");
            service = GoogleTaskHelper.getService();
            new TaskAsynTask().execute(this);
        }
    }



    private void setupActionbar() {
        ActionBar actionBar= getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.white)));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color='" + getResources().getColor(R.color.cyan) + "'><b>TASK</b></font>"));


    }

    public long getTaskGroupId() {
        return taskGroupId;
    }

    public Tasks getService() {
        return service;
    }
}