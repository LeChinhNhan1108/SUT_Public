package com.nhan.whattodo.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.TaskList;
import com.nhan.whattodo.R;
import com.nhan.whattodo.asyntask.TaskListAsynTask;
import com.nhan.whattodo.fragment.TGListFragment;
import com.nhan.whattodo.utils.DialogUtils;
import com.nhan.whattodo.utils.GoogleTaskHelper;
import com.nhan.whattodo.utils.GoogleTaskManager;
import com.nhan.whattodo.utils.L;

import java.util.ArrayList;

/**
 * Created by ivanle on 6/29/14.
 */
public class TaskListActivity extends Activity {

    private Tasks service;
    private ArrayList<TaskList> taskLists;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_group_activity);
        setupActionbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int googleServiceAvailable = GoogleTaskHelper.checkGooglePlayServiceAvailability(this);
        service = GoogleTaskHelper.getTaskService(this, googleServiceAvailable);

        if (service != null) {
            if (taskLists == null){
                DialogUtils.showDialog(DialogUtils.DialogType.PROGRESS_DIALOG, this, getString(R.string.wait_for_sync));
                new TaskListAsynTask().execute(this);
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        DialogUtils.dismissDialog(DialogUtils.DialogType.PROGRESS_DIALOG);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_group_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                L.d(" Clear ALL TL");
                GoogleTaskManager.clearAllTaskList(getService());
            }
        }).start();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GoogleTaskHelper.onCredentialActivityResult(this, requestCode, resultCode, data);
    }

    private void setupActionbar() {
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.white)));
        actionBar.setTitle(Html.fromHtml("<font color='" + getResources().getColor(R.color.cyan) + "'><b>TASK GROUP</b></font>"));

    }

    public void showTGFragment(ArrayList<TaskList> taskLists) {
        DialogUtils.dismissDialog(DialogUtils.DialogType.PROGRESS_DIALOG);
        if (taskLists == null) return;
        this.taskLists = taskLists;
        getFragmentManager().beginTransaction().replace(R.id.taskGroupFragmentContainer, TGListFragment.newInstance(taskLists)).commit();
    }


    public Tasks getService() {
        return service;
    }
}