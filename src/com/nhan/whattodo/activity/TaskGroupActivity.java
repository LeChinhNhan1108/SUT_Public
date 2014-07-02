package com.nhan.whattodo.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;
import com.nhan.whattodo.R;
import com.nhan.whattodo.asyntask.TaskGroupAsynTask;
import com.nhan.whattodo.fragment.TGListFragment;
import com.nhan.whattodo.fragment.TaskFragment;
import com.nhan.whattodo.utils.DialogUtils;
import com.nhan.whattodo.utils.GoogleTaskHelper;
import com.nhan.whattodo.utils.L;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by ivanle on 6/29/14.
 */
public class TaskGroupActivity extends Activity {

    private Tasks service;

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
        service = GoogleTaskHelper.getTaskService(this,googleServiceAvailable);

        if (service != null){
            DialogUtils.showDialog(DialogUtils.DialogType.PROGRESS_DIALOG,this,"Wait");
            new TaskGroupAsynTask().execute(this);
        }
    }

    public void showTGFragment(ArrayList<String> list){
        if (list.isEmpty()) return;
        DialogUtils.dismissDialog(DialogUtils.DialogType.PROGRESS_DIALOG);
        getFragmentManager().beginTransaction().replace(R.id.taskGroupFragmentContainer, TGListFragment.newInstance(list)).commit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GoogleTaskHelper.onCredentialActivityResult(this,requestCode,resultCode,data);
    }

    private void setupActionbar() {
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.white)));
        actionBar.setTitle(Html.fromHtml("<font color='" + getResources().getColor(R.color.cyan) + "'><b>TASK GROUP</b></font>"));

    }

    public Tasks getService() {
        return service;
    }
}