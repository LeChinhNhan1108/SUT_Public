package com.nhan.whattodo.data_manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.Tasks;
import com.nhan.whattodo.utils.L;

import java.util.ArrayList;

/**
 * Created by ivanle on 7/2/14.
 */
public class TaskListTable implements BaseColumns {

    public static final String TABLE_NAME = "TaskGroup";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_REMOTE_ID = "remote_id";

    static final String CREATE_TABLE = "create table " + TABLE_NAME + " (" +
            _ID + " integer primary key autoincrement," +
            FIELD_TITLE + " text not null," +
            FIELD_REMOTE_ID + " text" +
            ");";

    public static ArrayList<TaskList> getAllTaskList(Context context) {
        L.e("Get all task list from DB");
        SQLiteDatabase db = MyHelper.getSQLiteInstance(context);
        Cursor c = db.query(false, TABLE_NAME, null, null, null, null, null, _ID, null);
        ArrayList<TaskList> items = new ArrayList<TaskList>();
        if (!c.moveToFirst()) return null;
        while (!c.isAfterLast()) {
            TaskList item = new TaskList();
            item.set(_ID,c.getLong(c.getColumnIndex(_ID)));
            item.setTitle(c.getString(c.getColumnIndex(FIELD_TITLE)));
            item.setId(c.getString(c.getColumnIndex(FIELD_REMOTE_ID)));
            items.add(item);
            c.moveToNext();
        }
        return items;
    }

    public static long insertTaskList(Context context, TaskList taskList) {
        L.e("Insert to db");
        SQLiteDatabase db = MyHelper.getSQLiteInstance(context);
        ContentValues values = new ContentValues();
        values.put(FIELD_TITLE, taskList.getTitle());
        values.put(FIELD_REMOTE_ID, taskList.getId());
        return db.insert(TABLE_NAME, null, values);
    }


    public long getTaskListID(TaskList tl){
        return (Long) tl.get(_ID);
    }


}
