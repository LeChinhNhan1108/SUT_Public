package com.nhan.whattodo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.nhan.whattodo.activity.TaskGroupActivity;
import com.nhan.whattodo.utils.L;


public class MainActivity extends Activity {
    TextView flashText1;
    TextView flashText2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        flashText1 = (TextView) findViewById(R.id.flashText1);
        flashText2 = (TextView) findViewById(R.id.flashText2);

        AnimationSet fadeIn = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.fade_in);
        flashText1.setAnimation(fadeIn);

        AnimationSet slideFromLeft = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.slide_from_left);
        flashText2.setAnimation(slideFromLeft);

        fadeIn.start();
        slideFromLeft.start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(new Intent(MainActivity.this, TaskGroupActivity.class));
            }
        }, 2000);
    }
}
