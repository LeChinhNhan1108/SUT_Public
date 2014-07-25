package com.nhan.whattodo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.tasks.model.Task;
import com.nhan.whattodo.R;
import com.nhan.whattodo.data_manager.TaskListTable;
import com.nhan.whattodo.data_manager.TaskTable;
import com.nhan.whattodo.fragment.AddTaskFragment;
import com.nhan.whattodo.utils.GoogleTaskHelper;
import com.nhan.whattodo.utils.GoogleTaskManager;
import com.nhan.whattodo.utils.L;
import com.nhan.whattodo.utils.Utils;

import java.util.Calendar;
import java.util.Collections;

/**
 * Created by ivanle on 7/8/14. All rights reserved
 */
public class MessageReceiver extends BroadcastReceiver {

    public static final String SOURCE = "MessageReceiver";
    public static final String KEY_SOURCE = "source";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle pduBundle = intent.getExtras();
        Object[] pdus = (Object[]) pduBundle.get("pdus");
        SmsMessage messages = SmsMessage.createFromPdu((byte[]) pdus[0]);

        L.e("Receive Message " + messages.getMessageBody());

        if (messages.getMessageBody().contains(context.getString(R.string.SMS_HEADER))) {
            String messageBody = messages.getMessageBody();

            String[] tokens = messageBody.split(AddTaskFragment.DELIMETER_NL);
            String title = tokens[1].split(AddTaskFragment.DELIMETER1)[1];
            String dueDate = tokens[2].split(AddTaskFragment.DELIMETER1)[1];
            String time = tokens[3].split(AddTaskFragment.DELIMETER1)[1];
            final int priority = TaskTable.getPriorityFromString(tokens[4].split(AddTaskFragment.DELIMETER1)[1]);
            final long group = Long.parseLong(tokens[5].split(AddTaskFragment.DELIMETER1)[1]);
            String note = tokens.length == 2 ? tokens[6].split(AddTaskFragment.DELIMETER1)[1] : "";


            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Utils.convertStringToDateTime(dueDate + " " + time));

            final Task task = new Task();
            task.setTitle(title);
            task.setDue(new DateTime(calendar.getTime()));
            task.setStatus(TaskTable.STATUS_NEED_ACTION);
            if (note != null && !note.isEmpty())
                task.setNotes(note);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(context, Collections.singletonList(TasksScopes.TASKS));
                    String accName = GoogleTaskHelper.getAccFromSharePref(context);
                    credential.setSelectedAccountName(accName);

                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
                    Tasks service = new Tasks.Builder(httpTransport, jsonFactory, credential).build();

                    String remoteParentId = TaskListTable.getTaskListRemoteIDByLocalID(context, group);
                    Task remoteTask = GoogleTaskManager.insertTask(service, remoteParentId, task);

                    if (remoteTask != null) {
                        task.set(TaskTable.FIELD_PRIORITY, priority);
                        task.set(TaskTable.FIELD_GROUP, group);
                        task.set(TaskTable.FIELD_REMOTE_ID, remoteTask.getId());

                        TaskTable.insertTask(context, task);
                    }
                }
            }).start();
            abortBroadcast();
        } else {
            L.e("NOT Get Message");
        }
    }
}
