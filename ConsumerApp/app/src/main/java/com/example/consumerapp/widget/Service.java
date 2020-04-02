package com.example.consumerapp.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class Service extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackFactory(this.getApplicationContext());
    }
}
