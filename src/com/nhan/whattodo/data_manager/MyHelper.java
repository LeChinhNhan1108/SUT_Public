package com.nhan.whattodo.data_manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.nhan.whattodo.utils.L;

/**
 * Created by ivanle on 7/2/14.
 */
public class MyHelper extends SQLiteOpenHelper {


    public static final String DB_NAME = "WhatToDo";
    private static int VERSION = 1;

    public MyHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    private static MyHelper myHelper;

    public static SQLiteDatabase getSQLiteInstance(Context context){
       if (myHelper == null){
           myHelper = new MyHelper(context);
       }
        return myHelper.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TaskListTable.CREATE_TABLE);
        db.execSQL(TaskTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
