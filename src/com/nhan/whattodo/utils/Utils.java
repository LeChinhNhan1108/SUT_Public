package com.nhan.whattodo.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import com.google.api.client.util.DateTime;
import com.nhan.whattodo.R;
import com.nhan.whattodo.data_manager.TaskTable;
import com.nhan.whattodo.receiver.AlarmReceiver;
import com.nhan.whattodo.receiver.WidgetProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ivanle on 7/3/14.
 */
public class Utils {

    /* Date Time Format */

    private static final String DATE_PATTERN = "EEE, dd-MM-yyyy";
    private static final String TIME_PATTERN = "HH:mm";

    private static final String DATE_TIME_PATTERN = "EEE, dd-MM-yyyy HH:mm";



    public static String convertDateToString(long value){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);
        Date date  = new Date(value);
        return simpleDateFormat.format(date);
    }

    public static String convertTimeToString(long value){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_PATTERN);
        Date date  = new Date(value);
        return simpleDateFormat.format(date);
    }

    public static long convertStringToDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);
        try {
            return simpleDateFormat.parse(date).getTime();
        } catch (ParseException e) {
        }
        return 0;
    }

    public static long convertStringToTime(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_PATTERN);
        try {
            return simpleDateFormat.parse(date).getTime();
        } catch (ParseException e) {

        }
        return 0;
    }

    public static long convertStringToDateTime(String dateTime){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_PATTERN);
        try {
            return simpleDateFormat.parse(dateTime).getTime();
        } catch (ParseException e) {

        }
        return 0;
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

    // Connectivity Detector
    public static boolean isConnectedToTheInternet(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo() != null;
    }

    public static void updateAppWidget(Context context){
        int ids[] = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, WidgetProvider.class));
        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(ids, R.id.listViewWidget);
    }



}
