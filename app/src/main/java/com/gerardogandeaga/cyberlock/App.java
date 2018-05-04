package com.gerardogandeaga.cyberlock;

import android.app.Application;
import android.content.Context;

import com.gerardogandeaga.cyberlock.database.DatabaseOpenHelper;

import net.sqlcipher.database.SQLiteDatabase;

/**
 * @author gerardogandeaga
 */
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
        Database = new DatabaseOpenHelper(this);
    }

    public static Context getContext() {
        return Context;
    }
}
