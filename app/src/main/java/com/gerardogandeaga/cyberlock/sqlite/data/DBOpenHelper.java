package com.gerardogandeaga.cyberlock.sqlite.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE = "data.db";
    static final String TABLE = "data";
    private static final int VERSION = 1;

    DBOpenHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE data(" +

                "date INTEGER PRIMARY KEY, " +
                "type TEXT, " +
                "colour TEXT, " +
                "label TEXT, " +
                "content TEXT" +
                ");");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
