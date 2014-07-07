package com.nhan.whattodo.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import com.nhan.whattodo.R;
import com.nhan.whattodo.activity.TaskListActivity;
import com.nhan.whattodo.data_manager.TaskTable;

/**
 * Created by ivanle on 7/7/14. All rights reserved
 */
public class AlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE_FROM_BCR = 10;

    @Override
    public void onReceive(Context context, Intent intent) {
        String taskTitle = intent.getStringExtra(TaskTable.FIELD_TITLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(taskTitle)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(PendingIntent.getActivity(context, REQUEST_CODE_FROM_BCR, new Intent(context, TaskListActivity.class), PendingIntent.FLAG_ONE_SHOT))
        ;
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(REQUEST_CODE_FROM_BCR, builder.build());
    }


}
