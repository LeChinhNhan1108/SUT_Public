package com.nhan.whattodo.fragment;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.*;
import android.widget.*;
import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.nhan.whattodo.R;
import com.nhan.whattodo.activity.TaskActivity;
import com.nhan.whattodo.adapter.TaskListAdapter;
import com.nhan.whattodo.data_manager.TaskListTable;
import com.nhan.whattodo.data_manager.TaskTable;
import com.nhan.whattodo.receiver.AlarmReceiver;
import com.nhan.whattodo.utils.DialogFragment;
import com.nhan.whattodo.utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

/**
 * Created by ivanle on 7/4/14. All rights reserved
 */
public class AddTaskFragment extends Fragment implements View.OnClickListener {

    public static final int REQUEST_GET_CONTACT = 1;
    private Spinner taskListSpinner, spinnerPriority;
    private Button btnDue, btnRemind;
    private EditText edtTitle, edtNote;
    Calendar calendar;
    long dueDate, taskGroupId;
    ArrayList<Collaborator> collaborators;


    // Use when updating task
    Task taskToUpdate;

    public static AddTaskFragment newInstance(long taskGroupId) {

        AddTaskFragment addTaskFragment = new AddTaskFragment();
        addTaskFragment.taskGroupId = taskGroupId;
        return addTaskFragment;
    }

    public static AddTaskFragment newInstance(Task taskToUpdate) {

        AddTaskFragment addTaskFragment = new AddTaskFragment();
        addTaskFragment.taskToUpdate = taskToUpdate;
        return addTaskFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_task_layout, container, false);
        setUp(view);
        return view;
    }


    private void setUp(View view) {
        // Set up Spinner priority
        spinnerPriority = (Spinner) view.findViewById(R.id.spnPriority);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity()
                , android.R.layout.simple_spinner_dropdown_item, getPriorityString(TaskTable.PRIORITY.class));
        spinnerPriority.setAdapter(adapter);

        spinnerPriority.setSelection(TaskTable.PRIORITY.MEDIUM.ordinal());

        // Set up Spinner Task Group
        taskListSpinner = (Spinner) view.findViewById(R.id.spnTaskGroup);
        try {
            ArrayList<TaskList> taskLists = new GetAllTaskListFromDBAsyncTask().execute(getActivity()).get();
            TaskListAdapter adapter1 = new TaskListAdapter(getActivity(), R.layout.task_list_item, taskLists);
            taskListSpinner.setAdapter(adapter1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        // Since index = 0, id = 1
        taskListSpinner.setSelection((int) taskGroupId - 1);


        edtTitle = (EditText) view.findViewById(R.id.edtTitle);
        edtNote = (EditText) view.findViewById(R.id.edtNote);

        btnDue = (Button) view.findViewById(R.id.btnDue);
        btnRemind = (Button) view.findViewById(R.id.btnRemind);
        Button btnChooseColl = (Button) view.findViewById(R.id.btnChooseColl);
        ImageButton btnShowColl = (ImageButton) view.findViewById(R.id.btnShowColl);

        btnDue.setOnClickListener(this);
        btnRemind.setOnClickListener(this);
        btnChooseColl.setOnClickListener(this);
        btnShowColl.setOnClickListener(this);


        calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        dueDate = calendar.getTimeInMillis();
        collaborators = new ArrayList<Collaborator>();

        if (taskToUpdate != null) {

            spinnerPriority.setSelection(Integer.parseInt(taskToUpdate.get(TaskTable.FIELD_PRIORITY).toString()));
            taskListSpinner.setSelection(Integer.parseInt(taskToUpdate.get(TaskTable.FIELD_GROUP).toString()) - 1);

            edtTitle.setText(taskToUpdate.getTitle());
            edtNote.setText(taskToUpdate.getNotes());
            if (taskToUpdate.getDue() != null)
                dueDate = taskToUpdate.getDue().getValue();
            collaborators.addAll(stringToCollaborator((String) taskToUpdate.get(TaskTable.FIELD_COLLABORATOR)));
        }

        btnDue.setText("Due " + Utils.convertDateToString(dueDate));
        btnRemind.setText("Remind " + Utils.convertTimeToString(dueDate));


    }

    /* Date Picker Dialog */
    private DatePickerDialog datePickerDialog;

    private void btnDue() {
        if (datePickerDialog == null) {
            datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    calendar.set(year, monthOfYear, dayOfMonth);
                    dueDate = calendar.getTimeInMillis();
                    btnDue.setText("Due " + Utils.convertDateToString(dueDate));
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }
        datePickerDialog.show();
    }

    /* Time Picker Dialog */
    private TimePickerDialog timePickerDialog;

    private void btnRemind() {
        if (timePickerDialog == null) {
            timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);
                    dueDate = calendar.getTimeInMillis();
                    btnRemind.setText("Remind " + Utils.convertTimeToString(dueDate));
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        }
        timePickerDialog.show();
    }

    private void btnChooseColl() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Email.CONTENT_URI);
        startActivityForResult(intent, REQUEST_GET_CONTACT);
    }

    private void btnShowColl() {
        if (collaborators != null && collaborators.size() != 0) {
            CollaboratorFragment fragment = CollaboratorFragment.newInstance(collaborators);
            fragment.show(getFragmentManager(), "CollaboratorFragment");
        }
    }

    boolean notifyColl = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveTask:
                // No notify when update Task
                if (taskToUpdate != null) {
                    L.e("Update Task");
                    saveTask();
                } else {
                    notifyColl = false;
                    DialogFragment dialogFragment = DialogFragment.newInstance("Confirm Dialog", "Notify all collboratos",
                            new DialogFragment.IPositiveDialogClick() {
                                @Override
                                public void onClick() {
                                    notifyColl = true;
                                }
                            });
                    dialogFragment.setCancelable(false);
                    dialogFragment.setDismissAction(new DialogFragment.IOnDismiss() {
                        @Override
                        public void onDimiss() {
                            if (notifyColl) {
                                L.e("All collaborators " + collaborators.toString());
                            }
                            saveTask();
                        }
                    });


                    if (collaborators != null && !collaborators.isEmpty())
                        dialogFragment.show(getFragmentManager(), "Confirm Dialog");
                    else
                        saveTask();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String collaboratorsWithoutPhone = "";

    private void saveTask() {

        final String title = edtTitle.getText().toString();
        if (title.isEmpty()) return;

        final int priority = spinnerPriority.getSelectedItemPosition();
        final long dueDate = this.dueDate;
        final long parent_id = taskListSpinner.getSelectedItemId();
        final String collaborators = collaboratorToString(this.collaborators);
        String note = edtNote.getText().toString();

        DialogUtils.showDialog(DialogUtils.DialogType.PROGRESS_DIALOG, getActivity(), getString(R.string.wait_for_sync));
        if (taskToUpdate != null) {
            L.e("Before update " + taskToUpdate);
            final long currentGroupId = (Long) taskToUpdate.get(TaskTable.FIELD_GROUP);

            taskToUpdate.setTitle(title);
            taskToUpdate.setDue(new DateTime(dueDate));
            taskToUpdate.setNotes(note);
            taskToUpdate.set(TaskTable.FIELD_PRIORITY, priority);
            taskToUpdate.set(TaskTable.FIELD_GROUP, parent_id);
            taskToUpdate.set(TaskTable.FIELD_COLLABORATOR, collaborators);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String remoteParentId = TaskListTable.getTaskListRemoteIDByLocalID(getActivity(), currentGroupId);
                    L.e("Update " + remoteParentId + " " + taskToUpdate.getId());
                    Task updatedTask = GoogleTaskManager.updateTask(GoogleTaskHelper.getService(), remoteParentId
                            , taskToUpdate.getId(), taskToUpdate);
                    if (updatedTask != null) {
                        final int updateResult = TaskTable.updateTask(getActivity(), taskToUpdate);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogUtils.dismissDialog(DialogUtils.DialogType.PROGRESS_DIALOG);
                                if (updateResult != 0) {
                                    L.t(getActivity(), getString(R.string.update_success));
                                    if (priority == TaskTable.PRIORITY.HIGH.ordinal())
                                        Utils.setAlarm(getActivity(),dueDate, Integer.parseInt(taskToUpdate.get(TaskTable._ID)+"") , title);

                                    getActivity().onBackPressed();
                                } else {
                                    L.t(getActivity(), getString(R.string.update_error));
                                }
                            }
                        });
                    }
                }
            }).start();
        } else {

            final Task task = new Task();
            task.setTitle(title);
            task.setDue(new DateTime(dueDate));
            task.setNotes(note);
            task.setStatus(TaskTable.STATUS_NEED_ACTION);
            new Thread(new Runnable() {

                @Override
                public void run() {
                    String remoteParentId = TaskListTable.getTaskListRemoteIDByLocalID(getActivity(), parent_id);
                    Task remoteTask = GoogleTaskManager.insertTask(GoogleTaskHelper.getService(), remoteParentId, task);
                    if (remoteTask != null) {
                        remoteTask.set(TaskTable.FIELD_PRIORITY, priority);
                        remoteTask.set(TaskTable.FIELD_GROUP, parent_id);
                        remoteTask.set(TaskTable.FIELD_COLLABORATOR, collaborators);
                        remoteTask.set(TaskTable.FIELD_REMOTE_ID, remoteTask.getId());

                        final long insertedTaskId = TaskTable.insertTask(getActivity(), remoteTask);
                        if (notifyColl) {
                            for (Collaborator collaborator : AddTaskFragment.this.collaborators) {
                                if (collaborator.phone != null && !collaborator.phone.isEmpty()) {
                                    sendSMS(collaborator.phone, "Hello");
                                } else {
                                    collaboratorsWithoutPhone += collaborator.name + "\n";
                                }
                            }
                        } else {
                            L.e("No notification need");
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                L.t(getActivity(), getString(R.string.add_task_success));
                                DialogUtils.dismissDialog(DialogUtils.DialogType.PROGRESS_DIALOG);

                                if (!collaboratorsWithoutPhone.isEmpty())
                                    L.t(getActivity(), String.format(getString(R.string.contact_no_phone), collaboratorsWithoutPhone));

                                if (priority == TaskTable.PRIORITY.HIGH.ordinal())
                                    Utils.setAlarm(getActivity(),dueDate, (int)insertedTaskId, title);

                                getActivity().onBackPressed();
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                L.t(getActivity(), getString(R.string.add_task_error));
                            }
                        });
                    }
                }
            }).start();
        }
    }

    /* Helper functions & classes*/

    public static final String DELIMITER = ";";

    public String collaboratorToString(ArrayList<Collaborator> collaborators) {
        StringBuilder result = new StringBuilder();
        for (Collaborator collaborator : collaborators) {
            result.append(collaborator.toString()).append(DELIMITER);
        }
        if (!collaborators.isEmpty()) {
            result.deleteCharAt(result.lastIndexOf(DELIMITER));
        }
        return result.toString();
    }

    /* Get contact info for collaborators */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GET_CONTACT && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            String[] projection = {ContactsContract.CommonDataKinds.Email.CONTACT_ID, ContactsContract.CommonDataKinds.Email.ADDRESS
                    , ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER
            };

            Cursor cursor = getActivity().getContentResolver()
                    .query(uri, projection, null, null, null);
            cursor.moveToFirst();

            int column1 = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID);
            int column2 = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
            int column3 = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

            String contactID = cursor.getString(column1);
            String email = cursor.getString(column2);
            String name = cursor.getString(column3);

            cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null
                    , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{contactID}, null);

            String phone = "";
            if (cursor.moveToFirst()) {
                int column4 = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                phone = cursor.getString(column4);
            }
            collaborators.add(new Collaborator(contactID, name, email, phone));
        }
    }

    public ArrayList<Collaborator> stringToCollaborator(String coll) {
        ArrayList<Collaborator> temp = new ArrayList<Collaborator>();
        if (coll == null || coll.isEmpty()) return temp;
        String[] items = coll.split(DELIMITER);
        for (String item : items) {
            String[] split = item.split(",");
            temp.add(new Collaborator(split[0], split[1], split[2], split[3]));
        }
        return temp;
    }

//    public void readContacts() {
//        ContentResolver cr = getActivity().getContentResolver();
//        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
//                null, null, null, null);
//
//        if (cur.getCount() > 0) {
//            while (cur.moveToNext()) {
//                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
//                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//
//                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
//                    L.d("name : " + name + ", ID : " + id);
//
//                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
//                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
//                            new String[]{id}, null);
//                    while (pCur.moveToNext()) {
//                        String phone = pCur.getString(
//                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                        L.d("phone" + phone);
//                    }
//                    pCur.close();
//
//                    // get email and type
//
//                    Cursor emailCur = cr.query(
//                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
//                            null,
//                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
//                            new String[]{id}, null);
//                    while (emailCur.moveToNext()) {
//                        // This would allow you get several email addresses
//                        // if the email addresses were stored in an array
//                        String email = emailCur.getString(
//                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//                        String emailType = emailCur.getString(
//                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
//
//                        L.d("Email " + email + " Email Type : " + emailType);
//                    }
//                    emailCur.close();
//
//
//                }
//            }
//        }
//    }

    public class Collaborator {

        public String contact_id, name, email, phone;

        public Collaborator(String contact_id, String name, String email, String phone) {
            this.contact_id = contact_id;
            this.name = name;
            this.email = email;
            this.phone = phone;
        }

        @Override
        public String toString() {
            return contact_id + "," + name + "," + email + "," + phone;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_task_fragment_menu, menu);
        menu.findItem(R.id.addTask).setVisible(false);
        menu.findItem(R.id.updateTask).setVisible(false);
        menu.findItem(R.id.removeTask).setVisible(false);
    }

    public static String[] getPriorityString(Class<? extends Enum<?>> e) {
        L.e(Arrays.toString(e.getEnumConstants()));
        return Arrays.toString(e.getEnumConstants()).replaceAll("^.|.$", "").split(", ");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDue:
                btnDue();
                break;
            case R.id.btnRemind:
                btnRemind();
                break;
            case R.id.btnChooseColl:
                btnChooseColl();
                break;
            case R.id.btnShowColl:
                btnShowColl();
                break;
        }
    }

    public void sendSMS(String phone, String mess) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, getString(R.string.SMS_HEADER) + mess, null, null);
    }

    class GetAllTaskListFromDBAsyncTask extends AsyncTask<Activity, Void, ArrayList<TaskList>> {

        @Override
        protected ArrayList<TaskList> doInBackground(Activity... params) {
            TaskActivity activity = (TaskActivity) params[0];
            return TaskListTable.getAllTaskList(activity);
        }

    }
}