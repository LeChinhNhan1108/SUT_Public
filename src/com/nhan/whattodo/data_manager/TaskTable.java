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

    public static ArrayList<TaskModel> getAllTaskInTaskList(Context context, long id){

        ArrayList<TaskModel> tasks = null;
        SQLiteDatabase db = MyHelper.getSQLiteInstance(context);
        Cursor c = db.query(false,TABLE_NAME,null,FIELD_GROUP + "="+ id,null,null,null,null,null);
        if (!c.moveToFirst()) return tasks;

        tasks = new ArrayList<TaskModel>();

        while (!c.isAfterLast()){

            Task task = new Task();

            task.set(TaskTable._ID,c.getLong(c.getColumnIndex(TaskTable._ID)));
            task.setTitle(c.getString(c.getColumnIndex(FIELD_TITLE)));

            String dueDate = c.getString(c.getColumnIndex(FIELD_DUE_DATE));
            if (dueDate != null && !dueDate.isEmpty())task.setDue(new DateTime(dueDate));

            task.setNotes(c.getString(c.getColumnIndex(FIELD_NOTE)));
            task.setStatus(c.getString(c.getColumnIndex(FIELD_COMPLETION_STATUS)));
            task.setId(c.getString(c.getColumnIndex(FIELD_REMOTE_ID)));

            TaskModel model = new TaskModel(c.getInt(c.getColumnIndex(FIELD_PRIORITY)),c.getLong(c.getColumnIndex(FIELD_GROUP)), task);
            tasks.add(model);

            c.moveToNext();
        }
        return tasks;
    }

    public static long insertTask(Context context, TaskModel task) {
        SQLiteDatabase db = MyHelper.getSQLiteInstance(context);
        ContentValues values = new ContentValues();

        values.put(FIELD_TITLE, task.getTask().getTitle());
        values.put(FIELD_DUE_DATE, task.getTask().getDue() != null ? task.getTask().getDue().toString() : "");
        values.put(FIELD_NOTE, task.getTask().getNotes() != null ? task.getTask().getNotes() : "");
        values.put(FIELD_PRIORITY, task.getPriority());
        values.put(FIELD_COLLABORATOR, convertCollaboratorToString(task.getCollaborators()));
        values.put(FIELD_GROUP, task.getGroup_id());
        values.put(FIELD_COMPLETION_STATUS, task.getTask().getStatus());
        values.put(FIELD_REMOTE_ID, task.getTask().getId());

        return db.insert(TABLE_NAME, null, values);
    }


    public static void updateTask(Context context, TaskModel newModel){
        SQLiteDatabase db = MyHelper.getSQLiteInstance(context);
        ContentValues values  = new ContentValues();

        L.e("Update task");
        values.put(FIELD_TITLE, newModel.getTask().getTitle());
        values.put(FIELD_DUE_DATE, newModel.getTask().getDue() != null ? newModel.getTask().getDue().toString() : "");
        values.put(FIELD_NOTE, newModel.getTask().getNotes() != null ? newModel.getTask().getNotes() : "");
        values.put(FIELD_PRIORITY, newModel.getPriority());
        values.put(FIELD_COLLABORATOR, convertCollaboratorToString(newModel.getCollaborators()));
        values.put(FIELD_GROUP, newModel.getGroup_id());
        values.put(FIELD_COMPLETION_STATUS, newModel.getTask().getStatus());
        values.put(FIELD_REMOTE_ID, newModel.getTask().getId());

        int result = db.update(TABLE_NAME,values,_ID + "=" + newModel.getTask().get(TaskTable._ID),null);
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

}
