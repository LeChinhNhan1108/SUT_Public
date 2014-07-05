package com.nhan.whattodo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.nhan.whattodo.R;
import com.nhan.whattodo.activity.TaskActivity;
import com.nhan.whattodo.adapter.TaskGroupSpinnerAdapter;
import com.nhan.whattodo.adapter.TaskListAdapter;
import com.nhan.whattodo.data_manager.TaskListTable;
import com.nhan.whattodo.data_manager.TaskTable;
import com.nhan.whattodo.utils.L;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * Created by ivanle on 7/4/14.
 */
public class AddTaskFragment extends Fragment {
    Spinner taskListSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_task_layout,container,false);

        // Set up Spinner priority

        Spinner spinnerPriority = (Spinner) view.findViewById(R.id.spnPriority);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity()
                ,android.R.layout.simple_spinner_dropdown_item,getPriorityString(TaskTable.PRIORITY.class));
        spinnerPriority.setAdapter(adapter);

        // Set up Spinner Task Group
        taskListSpinner = (Spinner) view.findViewById(R.id.spnTaskGroup);
        L.d("Start exe");
        try {
            ArrayList<TaskList> taskLists = new GetAllTaskListFromDBAsynTask().execute(getActivity()).get();
            L.e("Get data " + (taskLists != null));
            ArrayList<String> data = new ArrayList<String>();

            for (TaskList taskList : taskLists) {
                data.add(taskList.getTitle());
            }

            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, data);
            taskListSpinner.setAdapter(adapter1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        L.d("Stop Exe");
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_task_fragment_menu,menu);
        menu.findItem(R.id.addTask).setVisible(false);
    }
    public static String[] getPriorityString(Class<? extends Enum<?>> e) {
        L.e(Arrays.toString(e.getEnumConstants()));
        return Arrays.toString(e.getEnumConstants()).replaceAll("^.|.$", "").split(", ");
    }


    class GetAllTaskListFromDBAsynTask extends AsyncTask<Activity,Void,ArrayList<TaskList>>{

        @Override
        protected ArrayList<TaskList> doInBackground(Activity... params) {
            TaskActivity activity = (TaskActivity) params[0];
            return TaskListTable.getAllTaskList(activity);
        }

    }



}