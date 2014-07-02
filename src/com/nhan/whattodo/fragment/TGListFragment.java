package com.nhan.whattodo.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.google.api.services.tasks.model.TaskList;
import com.nhan.whattodo.R;
import com.nhan.whattodo.adapter.TaskListAdapter;
import com.nhan.whattodo.utils.L;

import java.util.ArrayList;

/**
 * Created by ivanle on 6/29/14.
 */
public class TGListFragment extends ListFragment {

    private ArrayList<TaskList> taskLists;

    public static TGListFragment newInstance(ArrayList<TaskList> taskLists) {
        TGListFragment fragment = new TGListFragment();
        fragment.taskLists = taskLists;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TaskListAdapter adapter = new TaskListAdapter(getActivity(), R.layout.task_list_item,taskLists);
        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        L.t(getActivity(), taskLists.get(position).toString());
    }
}