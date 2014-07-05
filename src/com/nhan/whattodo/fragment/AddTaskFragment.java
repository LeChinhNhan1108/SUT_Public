package com.nhan.whattodo.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.nhan.whattodo.utils.GoogleTaskHelper;
import com.nhan.whattodo.utils.GoogleTaskManager;
import com.nhan.whattodo.utils.L;
import com.nhan.whattodo.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

/**
 * Created by ivanle on 7/4/14.
 */
public class AddTaskFragment extends Fragment implements View.OnClickListener {

    public static final int REQUEST_GET_CONTACT = 1;
    private Spinner taskListSpinner, spinnerPriority;
    private Button btnDue, btnRemind, btnChooseColl;
    private ImageButton btnShowColl;
    private EditText edtTitle, edtNote;
    Calendar calendar;
    long dueDate, taskGroupId;
    ArrayList<Collaborator> collaborators;


    public static AddTaskFragment newInstance(long taskGroupId) {

        AddTaskFragment addTaskFragment = new AddTaskFragment();
        addTaskFragment.taskGroupId = taskGroupId;
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
            ArrayList<TaskList> taskLists = new GetAllTaskListFromDBAsynTask().execute(getActivity()).get();
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
        btnChooseColl = (Button) view.findViewById(R.id.btnChooseColl);
        btnShowColl = (ImageButton) view.findViewById(R.id.btnShowColl);

        btnDue.setOnClickListener(this);
        btnRemind.setOnClickListener(this);
        btnChooseColl.setOnClickListener(this);
        btnShowColl.setOnClickListener(this);


        calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        dueDate = calendar.getTimeInMillis();
        collaborators = new ArrayList<Collaborator>();
        btnDue.setText("Due " + Utils.convertDateToString(dueDate));
        btnRemind.setText("Remind " + Utils.convertTimeToString(dueDate));
    }

    /* Date Picker Dialog */
    private DatePickerDialog datePickerDialog;

    private void btnDue(View v) {
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

    private void btnRemind(View v) {
        if (timePickerDialog == null) {
            timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    dueDate = calendar.getTimeInMillis();
                    btnRemind.setText("Remind " + Utils.convertTimeToString(dueDate));
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        }
        timePickerDialog.show();
    }

    private void btnChooseColl(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Email.CONTENT_URI);
        startActivityForResult(intent, REQUEST_GET_CONTACT);
    }

    private void btnShowColl(View v) {
        if (collaborators != null && collaborators.size() != 0) {
            CollaboratorFragment fragment = CollaboratorFragment.newInstance(collaborators);
            fragment.show(getFragmentManager(), "CollaboratorFragment");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveTask:
                saveTask();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();
        L.e("Resume");
        if (collaborators == null || collaborators.isEmpty()) return;
        L.e("Cooll " + collaborators.toString());
    }

    private void saveTask() {
        String title = edtTitle.getText().toString();
        if (title.isEmpty()) return;

        int priority = spinnerPriority.getSelectedItemPosition();
        long dueDate = this.dueDate;
        final long parent_id = taskListSpinner.getSelectedItemId();
        String collaborators = collaboratorToString(this.collaborators);
        String note = edtNote.getText().toString();

        final Task task = new Task();
        task.setTitle(title);
        task.set(TaskTable.FIELD_PRIORITY, priority);
        task.setDue(new DateTime(dueDate));
        task.set(TaskTable.FIELD_GROUP, parent_id);
        task.set(TaskTable.FIELD_COLLABORATOR, collaborators);
        task.setNotes(note);


//        L.e("Task added " + task.toString());

        long result = TaskTable.insertTask(getActivity(), task);
        if (result != -1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    TaskListTable.getTaskListRemoteIDByLocalID(getActivity(), parent_id);
                    String remoteId = TaskListTable.getTaskListRemoteIDByLocalID(getActivity(), parent_id);
//                    L.e("Parent id " + parent_id);
//                    L.e("Remote id " + remoteId);
                    GoogleTaskManager.insertTask(GoogleTaskHelper.getService(), remoteId, task);
                }
            }).start();
            getActivity().onBackPressed();
        } else {
            L.e("Hic Error");
        }

    }

    public static final String DELIMITER = ";";

    public String collaboratorToString(ArrayList<Collaborator> collaborators) {
        StringBuilder result = new StringBuilder();
        for (Collaborator collaborator : collaborators) {
            result.append(collaborator.toString() + DELIMITER);
        }
        if (collaborators != null && !collaborators.isEmpty()) {
            result.deleteCharAt(result.lastIndexOf(DELIMITER));
        }
        return result.toString();
    }

    public void readContacts() {
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    L.d("name : " + name + ", ID : " + id);

                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phone = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        L.d("phone" + phone);
                    }
                    pCur.close();

                    // get email and type

                    Cursor emailCur = cr.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCur.moveToNext()) {
                        // This would allow you get several email addresses
                        // if the email addresses were stored in an array
                        String email = emailCur.getString(
                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        String emailType = emailCur.getString(
                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                        L.d("Email " + email + " Email Type : " + emailType);
                    }
                    emailCur.close();


                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_GET_CONTACT && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            L.e("REsult code " + uri.toString());
            String[] projection = {ContactsContract.CommonDataKinds.Email.CONTACT_ID, ContactsContract.CommonDataKinds.Email.ADDRESS
                    , ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER
            };

            Cursor cursor = getActivity().getContentResolver()
                    .query(uri, projection, null, null, null);
            cursor.moveToFirst();

            L.e("Cursor " + cursor.getCount());

            int column1 = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID);
            int column2 = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
            int column3 = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

            String contactID = cursor.getString(column1);
            String email = cursor.getString(column2);
            String name = cursor.getString(column3);

            cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null
                    , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{contactID}, null);

            cursor.moveToFirst();

            int column4 = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            String phone = cursor.getString(column4);

            L.e(contactID + " " + email + " " + name + " " + phone);

            collaborators.add(new Collaborator(contactID, name, email, phone));
        }

    }

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
                btnDue(v);
                break;
            case R.id.btnRemind:
                btnRemind(v);
                break;
            case R.id.btnChooseColl:
                btnChooseColl(v);
                break;
            case R.id.btnShowColl:
                btnShowColl(v);
                break;
        }
    }

    class GetAllTaskListFromDBAsynTask extends AsyncTask<Activity, Void, ArrayList<TaskList>> {

        @Override
        protected ArrayList<TaskList> doInBackground(Activity... params) {
            TaskActivity activity = (TaskActivity) params[0];
            return TaskListTable.getAllTaskList(activity);
        }

    }

    class GetContactAsync extends AsyncTask<Activity, Void, Void> {

        @Override
        protected Void doInBackground(Activity... params) {
            return null;
        }


    }
}