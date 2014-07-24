package com.nhan.whattodo.service;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.google.api.services.tasks.model.Task;
import com.nhan.whattodo.R;
import com.nhan.whattodo.data_manager.TaskTable;
import com.nhan.whattodo.utils.L;
import com.nhan.whattodo.utils.Utils;

import java.util.ArrayList;

/**
 * Created by ivanle on 7/23/14. All rights reserved
 */
/**
 * If you are familiar with Adapter of ListView,this is the same as adapter
 * with few changes
 *
 */
public class ListProvider implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList<Task> listItemList = new ArrayList<Task>();
    private Context context = null;

    public ListProvider(Context context) {
        this.context = context;
        populateListItem();
    }

    private void populateListItem() {
        listItemList = TaskTable.getAllTask(context);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        populateListItem();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public RemoteViews getViewAt(int position) {

        L.e("getViewAt");

        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.widget_list_item);
        Task listItem = listItemList.get(position);
        remoteView.setTextViewText(R.id.tvTaskTitle, listItem.getTitle());

        int priority = (Integer)listItem.get(TaskTable.FIELD_PRIORITY);
        if (priority == TaskTable.PRIORITY.HIGH.ordinal())
            remoteView.setTextColor(R.id.tvTaskTitle,context.getResources().getColor(android.R.color.holo_red_dark));
        else if (priority == TaskTable.PRIORITY.MEDIUM.ordinal())
            remoteView.setTextColor(R.id.tvTaskTitle,context.getResources().getColor(android.R.color.holo_orange_light));
        else if (priority == TaskTable.PRIORITY.LOW.ordinal())
            remoteView.setTextColor(R.id.tvTaskTitle, context.getResources().getColor(android.R.color.black));

        remoteView.setTextViewText(R.id.tvTaskDue,
                listItem.getDue() != null ? Utils.convertDateToString(listItem.getDue().getValue()) : "");

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }
}
