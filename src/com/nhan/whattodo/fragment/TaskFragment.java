package com.nhan.whattodo.fragment;

import android.accounts.*;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.nhan.whattodo.utils.L;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ivanle on 6/30/14.
 */
public class TaskFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView tv = new TextView(getActivity());
        tv.setText("Hello World");
        showDialog().show();
        return tv;
    }


    public Dialog showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select a Google account");
        AccountManager accountManager = AccountManager.get(getActivity());
        final Account[] accounts = accountManager.getAccountsByType("com.google");
        final int size = accounts.length;
        String[] names = new String[size];
        for (int i = 0; i < size; i++) {
            names[i] = accounts[i].name;
        }
        builder.setItems(names, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                gotAccount(accounts[which]);
            }


        });
        return builder.create();
    }

    String AUTH_TOKEN_TYPE = "oauth2:https://www.googleapis.com/auth/tasks https://www.googleapis.com/auth/tasks.readonly";

    private void gotAccount(Account account) {
        AccountManager accountManager = AccountManager.get(getActivity());
        accountManager.getAuthToken(account, AUTH_TOKEN_TYPE, null, getActivity(), new AccountManagerCallback<Bundle>() {
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    String token = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
                    useTasksAPI(token);
                } catch (OperationCanceledException e) {
                    L.e(e.getMessage());
                } catch (Exception e) {
                    L.e(e.getMessage());
                }
            }
        }, null);
    }


    public static final String CLIENT_ID = "219712857964-5e4ktktk5maurkjh9736p1dnahpogrm7.apps.googleusercontent.com";
    public static final String CLIENT_SECRET = "q9SA9xHH2r1OwTUHHZ1-wqO2";

    public void useTasksAPI(final String token) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    L.e(token);
//                    URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token="
//                            + token);
                    URL url = new URL("https://www.googleapis.com/tasks/v1/users/@me/lists");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    int serverCode = con.getResponseCode();
                    if (serverCode == 200) {
                        InputStream is = con.getInputStream();
                        byte[] bytes = new byte[is.available()];
                        L.e(new String(bytes));
                        is.close();
                        return;
                    } else if (serverCode == 401) {
                        GoogleAuthUtil.invalidateToken(getActivity(), token);
                        L.e("Server returned the following error code: " + serverCode);
                        return;
                    } else {
                        L.e("Server returned the following error code: " + serverCode);
                        return;
                    }
                } catch (IOException e) {
                    L.e(e.getMessage());
                }

            }
        }).start();

    }
}