package com.balloon;

import android.app.Application;


public class App extends Application {
    public static App singleton = null;

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }
}