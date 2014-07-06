package com.nhan.whattodo.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

/**
 * Created by ivanle on 7/1/14.
 */
public class DialogUtils {

    public enum DialogType {
        PROGRESS_DIALOG
    }

    private static ProgressDialog progressDialog;

    public static void showDialog(DialogType type, Activity activity, String mess) {

        if (progressDialog == null) progressDialog = new ProgressDialog(activity);
        switch (type) {
            case PROGRESS_DIALOG:
                progressDialog.setTitle(mess);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();
                break;
        }
    }
    public static void dismissDialog(DialogType type) {
        switch (type) {
            case PROGRESS_DIALOG:
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                break;
        }
    }




}
