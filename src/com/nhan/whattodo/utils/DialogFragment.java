package com.nhan.whattodo.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by ivanle on 7/6/14.
 */
public class DialogFragment extends android.app.DialogFragment {

    private String title;
    private String mess;

    public static DialogFragment newInstance(String title, String mess, IPositiveDialogClick positiveDialogClick) {
        DialogFragment dialogFragment = new DialogFragment();
        dialogFragment.title = title;
        dialogFragment.mess = mess;
        dialogFragment.positiveDialogClick = positiveDialogClick;
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(mess)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        positiveDialogClick.onClick();
                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        L.d("Dismiss ");
        dismissAction.onDimiss();
    }

    private IPositiveDialogClick positiveDialogClick;
    private IOnDismiss dismissAction;

    public void setPositiveDialogClick(IPositiveDialogClick positiveDialogClick) {
        this.positiveDialogClick = positiveDialogClick;
    }

    public void setDismissAction(IOnDismiss dismissAction) {
        this.dismissAction = dismissAction;
    }

    public interface IPositiveDialogClick {
        public void onClick();
    }

    public interface IOnDismiss {
        public void onDimiss();
    }
}
