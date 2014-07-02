package com.nhan.whattodo.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.nhan.whattodo.R;

import java.util.ArrayList;

/**
 * Created by ivanle on 6/29/14.
 */
public class TGListFragment extends ListFragment {

    ArrayList<String> list;

    public static TGListFragment newInstance(ArrayList<String> list) {
        TGListFragment fragment = new TGListFragment();
        fragment.list = list;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);
        setListAdapter(arrayAdapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}