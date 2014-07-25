package com.nhan.whattodo.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import com.nhan.whattodo.R;
import com.nhan.whattodo.service.WidgetService;
import com.nhan.whattodo.utils.L;

/**
 * Created by ivanle on 7/23/14. All rights reserved
 */
public class WidgetProvider extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager
            appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        L.e("Widget Update ");

        final int N = appWidgetIds.length;
        for (int i = 0; i < N; ++i) {
            RemoteViews remoteViews = updateWidgetListView(context,
                    appWidgetIds[i]);
            appWidgetManager.updateAppWidget(appWidgetIds[i],
                    remoteViews);
        }
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {

        L.e("Widget updateWidgetListView ");

        RemoteViews remoteViews = new RemoteViews(
                context.getPackageName(), R.layout.app_widget_layout);

        Intent svcIntent = new Intent(context, WidgetService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.setData(Uri.parse(
                svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(appWidgetId, R.id.listViewWidget,
                svcIntent);
        remoteViews.setEmptyView(R.id.listViewWidget, R.id.empty_view);

        return remoteViews;
    }

}
