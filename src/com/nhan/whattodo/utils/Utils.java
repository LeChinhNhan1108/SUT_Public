package com.nhan.whattodo.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.google.api.client.util.DateTime;
import com.nhan.whattodo.data_manager.TaskTable;
import com.nhan.whattodo.receiver.AlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ivanle on 7/3/14.
 */
public class Utils {

    /* Date Time Format */

    public static String convertDateToString(long value){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd-MM-yyyy");
        Date date  = new Date(value);
        return simpleDateFormat.format(date);
    }

    public static String convertTimeToString(long value){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date date  = new Date(value);
        return simpleDateFormat.format(date);
    }

    public static void setAlarm(Context context, long triggerAt, int taskId, String taskTitle) {

        L.e("Set Alarm at " + new DateTime(triggerAt).toString());
        L.e("Set Alarm at " + taskId + " " + taskTitle);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(TaskTable.FIELD_TITLE, taskTitle);
        PendingIntent operation = PendingIntent.getBroadcast(context, taskId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, operation);
    }

    public static void cancelAlarm(Context context, int taskId) {

        L.e("Cancel Alarm id: " + taskId);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent operation = PendingIntent.getBroadcast(context, taskId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(operation);
    }



}
