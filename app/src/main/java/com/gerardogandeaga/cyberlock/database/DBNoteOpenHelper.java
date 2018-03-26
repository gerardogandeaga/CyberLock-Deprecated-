package com.gerardogandeaga.cyberlock.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBNoteOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE = "data.db";
    static final String TABLE = "data";
    private static final int VERSION = 1;

    // values
    static final String FOLDER = "folder";
    static final String DATE = "date"; // order key
    static final String TYPE = "type";
    static final String COLOUR_TAG = "colour_tag";
    static final String LABEL = "label";
    static final String CONTENT = "content";

    DBNoteOpenHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + "(" +

//              KEY            DATA TYPE
                DATE +       " INTEGER PRIMARY KEY, " +
                FOLDER +     " BLOB, " +
                TYPE +       " BLOB, " +
                COLOUR_TAG + " BLOB, " +
                LABEL +      " BLOB, " +
                CONTENT +    " BLOB" +

                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
