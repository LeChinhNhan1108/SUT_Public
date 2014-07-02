package com.nhan.whattodo.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import com.nhan.whattodo.R;

/**
 * Created by ivanle on 6/29/14.
 */
public class TaskActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_activity);
        setupActionbar();
    }



    private void setupActionbar() {
        ActionBar actionBar= getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.white)));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color='" + getResources().getColor(R.color.cyan) + "'><b>TASK</b></font>"));


    }
}