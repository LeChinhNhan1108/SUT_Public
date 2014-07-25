package com.nhan.whattodo.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ivanle on 6/29/14.
 */
public class L {

    public static String APP_LOG_TAG = "what2do_log_tag";

    public static void d(String message) {
        Log.d(APP_LOG_TAG, message);
    }

    public static void e(String message) {
        Log.e(APP_LOG_TAG, message);
    }

    public static void i(String message) {
        Log.i(APP_LOG_TAG, message);
    }

    public static void t(Context c, String message) {
        Toast.makeText(c, message, Toast.LENGTH_LONG).show();
    }
}
