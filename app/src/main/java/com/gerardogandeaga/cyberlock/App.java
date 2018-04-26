package com.gerardogandeaga.cyberlock;

import android.app.Application;
import android.content.Context;

/**
 * @author gerardogandeaga
 */
public class App extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext() {
        return context;
    }
}
