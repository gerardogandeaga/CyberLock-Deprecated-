package com.gerardogandeaga.cyberlock.EncryptionFeatures.ContentDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class MasterDatabaseOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE = "data.db";
    public static final String TABLE = "data";
    public static final int VERSION = 1;

    public MasterDatabaseOpenHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE data(" +
                "date INTEGER PRIMARY KEY, " +
                "type TEXT, " +
                "label TEXT, " +
                "content TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
