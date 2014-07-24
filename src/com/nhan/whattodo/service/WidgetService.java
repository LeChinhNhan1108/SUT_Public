package com.nhan.whattodo.service;

import android.content.Intent;
import android.widget.RemoteViewsService;
import com.nhan.whattodo.utils.L;

/**
 * Created by ivanle on 7/23/14. All rights reserved
 */
public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        L.e("onGetViewFactory");
        return (new ListProvider(this.getApplicationContext()));
    }

}
