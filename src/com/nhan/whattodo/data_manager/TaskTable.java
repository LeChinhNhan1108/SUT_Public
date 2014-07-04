package com.nhan.whattodo.data_manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.nhan.whattodo.utils.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivanle on 7/3/14.
 */
public class TaskTable implements BaseColumns {

    public static final String TABLE_NAME = "Task";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_DUE_DATE = "due_date";
    public static final String FIELD_NOTE = "note";
    public static final String FIELD_PRIORITY = "priority";
    public static final String FIELD_COLLABORATOR = "collaborators";
    public static final String FIELD_GROUP = "group_id";
    public static final String FIELD_COMPLETION_STATUS = "completion";
    public static final String FIELD_REMOTE_ID = "remote_id";

    public static final String CREATE_TABLE = "create table " + TABLE_NAME + " (" +
            _ID + " integer primary key autoincrement," +
            FIELD_TITLE + " text not null," +
            FIELD_DUE_DATE + " text," +
            FIELD_NOTE + " text," +
            FIELD_PRIORITY + " integer," +
            FIELD_COLLABORATOR + " text," +
            FIELD_GROUP + " integer," +
            FIELD_COMPLETION_STATUS + " text," +
            FIELD_REMOTE_ID + " text" +
            ");";

    public static ArrayList<Task> getAllTaskInTaskList(Context context, long id){

        ArrayList<Task> tasks = null;
        SQLiteDatabase db = MyHelper.getSQLiteInstance(context);
        Cursor c = db.query(false,TABLE_NAME,null,FIELD_GROUP + "="+ id,null,null,null,null,null);

        if (!c.moveToFirst()) return tasks;

        tasks = new ArrayList<Task>();

        while (!c.isAfterLast()){

            Task task = new Task();

            task.set(TaskTable._ID,c.getLong(c.getColumnIndex(TaskTable._ID)));
            task.setTitle(c.getString(c.getColumnIndex(FIELD_TITLE)));

            String dueDate = c.getString(c.getColumnIndex(FIELD_DUE_DATE));
            if (dueDate != null && !dueDate.isEmpty())task.setDue(new DateTime(dueDate));

            task.setNotes(c.getString(c.getColumnIndex(FIELD_NOTE)));
            task.setStatus(c.getString(c.getColumnIndex(FIELD_COMPLETION_STATUS)));
            task.setId(c.getString(c.getColumnIndex(FIELD_REMOTE_ID)));

            task.set(FIELD_PRIORITY, c.getInt(c.getColumnIndex(FIELD_PRIORITY)));
            task.set(FIELD_GROUP,c.getLong(c.getColumnIndex(FIELD_GROUP)));
            task.set(FIELD_COLLABORATOR,c.getString(c.getColumnIndex(FIELD_COLLABORATOR)));

            tasks.add(task);

            c.moveToNext();
        }
        return tasks;
    }

    public static long insertTask(Context context, Task task) {
        SQLiteDatabase db = MyHelper.getSQLiteInstance(context);
        ContentValues values = new ContentValues();

        values.put(FIELD_TITLE, task.getTitle());
        values.put(FIELD_DUE_DATE, task.getDue() != null ? task.getDue().toString() : "");
        values.put(FIELD_NOTE, task.getNotes() != null ? task.getNotes() : "");
        values.put(FIELD_PRIORITY, (Integer)task.get(FIELD_PRIORITY));
        values.put(FIELD_COLLABORATOR, (String)task.get(FIELD_COLLABORATOR));
        values.put(FIELD_GROUP, (Long)task.get(FIELD_GROUP));
        values.put(FIELD_COMPLETION_STATUS, task.getStatus());
        values.put(FIELD_REMOTE_ID, task.getId());

        return db.insert(TABLE_NAME, null, values);
    }


    public static void updateTask(Context context, Task task){
        SQLiteDatabase db = MyHelper.getSQLiteInstance(context);
        ContentValues values  = new ContentValues();
        L.e("Update task");

        values.put(FIELD_TITLE, task.getTitle());
        values.put(FIELD_DUE_DATE, task.getDue() != null ? task.getDue().toString() : "");
        values.put(FIELD_NOTE, task.getNotes() != null ? task.getNotes() : "");
        values.put(FIELD_PRIORITY, (Integer)task.get(FIELD_PRIORITY));
        values.put(FIELD_COLLABORATOR, (String)task.get(FIELD_COLLABORATOR));
        values.put(FIELD_GROUP, (Long)task.get(FIELD_GROUP));
        values.put(FIELD_COMPLETION_STATUS, task.getStatus());
        values.put(FIELD_REMOTE_ID, task.getId());

        int result = db.update(TABLE_NAME,values,_ID + "=" + task.get(TaskTable._ID),null);
        L.e("Result "+ result);
    }

    public static int deleteTask(Context context, long id){
        SQLiteDatabase db = MyHelper.getSQLiteInstance(context);
        return db.delete(TABLE_NAME,_ID +"=" +id,null);
    }

    private static String convertCollaboratorToString(List<Integer> coll) {
        String temp = "";
        for (int i = 0; i < coll.size(); i++) {
            temp += (coll.get(i) + (i <= coll.size() - 1 ? "," : ""));
        }
        return temp;
    }


    public enum PRIORITY{
        HIGH, MEDIUM, LOW
    }

}
