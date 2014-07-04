package com.nhan.whattodo.utils;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.TasksScopes;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

/**
 * Created by ivanle on 7/1/14.
 */

/* This class deals with check Google Play Service Availability and get Account Credential*/
public class GoogleTaskHelper {

    public static final int GOOGLE_PLAY_SERVICE_REQUEST = 1;
    public static final int CREDENTIAL_REQUEST = 2;


    private static final String PREF_NAME = "What2do";
    private static final String KEY_ACC = "Account";

    private static GoogleAccountCredential credential;
    private static Tasks service;

    public static int checkGooglePlayServiceAvailability(Activity activity) {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
    }

    public static Tasks getTaskService(Activity activity, int result) {
        if (result != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(result, activity, GOOGLE_PLAY_SERVICE_REQUEST);
            return null;
        } else {
            L.d("Google play service OK");
            return getCredential(activity);
        }
    }

    public static Tasks getCredential(Activity activity) {
        if (credential == null) {
            credential = GoogleAccountCredential.usingOAuth2(activity, Collections.singletonList(TasksScopes.TASKS));
        }

        if (credential.getSelectedAccountName() == null) {
            String accName = getAccFromSharePref(activity);
            if (accName != null) {
                L.d("Get credential " + accName);
                credential.setSelectedAccountName(accName);
                return getService();
            } else {
                L.d("Get credential choose acc");
                activity.startActivityForResult(credential.newChooseAccountIntent(), CREDENTIAL_REQUEST);
                return null;
            }
        }
        return getService();
    }

    public static void onCredentialActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == CREDENTIAL_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            saveAccToPref(activity, accountName);
        }else if (resultCode == Activity.RESULT_CANCELED){
            activity.finish();
        }

    }

    public static void saveAccToPref(Activity activity, String accName) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACC, accName).commit();
    }


    public static String getAccFromSharePref(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ACC, null);
    }

    public static Tasks getService() {
        if (service == null) {
            HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
            service = new Tasks.Builder(httpTransport, jsonFactory, credential).build();
        }
        return service;
    }


}
