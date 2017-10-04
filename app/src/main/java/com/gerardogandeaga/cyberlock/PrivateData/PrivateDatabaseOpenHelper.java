package com.gerardogandeaga.cyberlock.PrivateData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class PrivateDatabaseOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE = "data.db";
    public static final String TABLE = "data";
    public static final int VERSION = 1;

    public PrivateDatabaseOpenHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE data(" +
                "lastlogin INTEGER PRIMARY KEY, " +
                "passcode TEXT, " +
                "cryptkey TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}