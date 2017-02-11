package com.example.avidavidov.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created by avi.davidov on 09/02/2017.
 */

public class MyService extends Service {

    int counter = 0;
    Thread thread;
    IBinder binder;
    MainActivity mainActivity;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class Mybinder extends Binder {
        MyService getService() {
            return MyService.this;
        }

    }

}