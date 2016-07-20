package com.examples.pj.einkaufsapp;

import android.app.Application;

/**
 * App Class
 */
public class EinkaufsApp extends Application {

    private static EinkaufsApp app;

    public static EinkaufsApp getApp() {
        return app;
    }

    public static synchronized void setApp(EinkaufsApp app) {
        EinkaufsApp.app = app;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        EinkaufsApp.setApp(this);
    }
}
