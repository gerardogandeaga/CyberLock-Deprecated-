package com.gerardogandeaga.cyberlock;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.gerardogandeaga.cyberlock.database.DatabaseOpenHelper;

import net.sqlcipher.database.SQLiteDatabase;

/**
 * @author gerardogandeaga
 *
 * keeps critical singular static instances for easy referencing
 */
@SuppressLint("StaticFieldLeak")
public class App extends Application {
    private static Context Context;
    private static DatabaseOpenHelper Database;

    public static DatabaseOpenHelper getDatabase() {
        return Database;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Context = this;

        // start the sql db
        SQLiteDatabase.loadLibs(this);

        /*
         this do is always created on application startup and is made static to only one instance of the
         db at all times for better performance */
        Database = new DatabaseOpenHelper(this);
    }

    public static Context getContext() {
        return Context;
    }
}
