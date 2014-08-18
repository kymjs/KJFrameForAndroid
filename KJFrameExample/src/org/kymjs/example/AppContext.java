package org.kymjs.example;

import org.kymjs.aframe.CrashHandler;

import android.app.Application;

public class AppContext extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.create(this);
    }
}
