package com.nhan.whattodo.fragment;

import android.app.*;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.api.client.util.DateTime;
import com.nhan.whattodo.MainActivity;
import com.nhan.whattodo.R;
import com.nhan.whattodo.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivanle on 7/5/14.
 */
public class CollaboratorFragment extends DialogFragment {


    ArrayList<AddTaskFragment.Collaborator> collaborators;
    private ICollBtnRemove iCollBtnRemove;

    public CollaboratorFragment() {
    }

    public static CollaboratorFragment newInstance(ArrayList<AddTaskFragment.Collaborator> collaborators){
        CollaboratorFragment fragment = new CollaboratorFragment();
        fragment.collaborators = collaborators;
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View convertView = inflater.inflate(R.layout.collaborator_fragment, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Collaborator List");
        ListView lv = (ListView) convertView.findViewById(R.id.collList);

        CollAdapter adapter = new CollAdapter(getActivity(),R.layout.collaborator_fragment_item,collaborators);
        lv.setAdapter(adapter);

        return alertDialog.create();
    }


    class CollAdapter extends ArrayAdapter<AddTaskFragment.Collaborator>{

        ArrayList<AddTaskFragment.Collaborator> collaborators;
        int resource;
        public CollAdapter(Context context, int resource, ArrayList<AddTaskFragment.Collaborator> objects) {
            super(context, resource, objects);
            this.collaborators = objects;
            this.resource = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null){
                v = ((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(resource, null);
            }

            TextView tvName = (TextView) v.findViewById(R.id.tvName);
            TextView tvEmail = (TextView) v.findViewById(R.id.tvEmail);
            ImageButton btnRemove = (ImageButton) v.findViewById(R.id.btnRemove);
            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    collaborators.remove(position);
                    notifyDataSetChanged();
                }
            });

            tvName.setText(collaborators.get(position).name);
            tvEmail.setText(collaborators.get(position).email);

            return v;
        }
    }

    public ICollBtnRemove getiCollBtnRemove() {
        return iCollBtnRemove;
    }

    public void setiCollBtnRemove(ICollBtnRemove iCollBtnRemove) {
        this.iCollBtnRemove = iCollBtnRemove;
    }

    public interface ICollBtnRemove{
        public void btnCollRemove();
    }
}










