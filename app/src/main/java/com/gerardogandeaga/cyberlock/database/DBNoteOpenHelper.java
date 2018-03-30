package com.gerardogandeaga.cyberlock.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBNoteOpenHelper extends SQLiteOpenHelper implements DBNoteConstants {
    private static final String DATABASE = "data.db";
    static final String TABLE = "data";
    private static final int VERSION = 1;

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
