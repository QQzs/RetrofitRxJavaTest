package com.zs.demo.retrofitrxjavatest;

import android.app.Application;
import android.content.Context;

/**
 * Created by edianzu on 2017/4/18.
 */

public class MyApp extends Application {
    private static MyApp app;

    public static Context getAppContext() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }
}
