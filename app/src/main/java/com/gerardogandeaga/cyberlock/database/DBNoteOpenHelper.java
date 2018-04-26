package com.gerardogandeaga.cyberlock.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gerardogandeaga.cyberlock.interfaces.DBNoteConstants;

/**
 * @author gerardogandeaga
 */
class DBNoteOpenHelper extends SQLiteOpenHelper implements DBNoteConstants {
    private static final String DATABASE = TABLE + ".db";
    private static final int VERSION = 1;

    DBNoteOpenHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + "(" +

//              KEY               DATA TYPE
                DATE_CREATED +  " INTEGER, " +
                DATE_MODIFIED + " INTEGER PRIMARY KEY, " +
                TRASHED +       " INTEGER, " +
                FOLDER +        " BLOB, " +
                TYPE +          " BLOB, " +
                COLOUR_TAG +    " BLOB, " +
                LABEL +         " BLOB, " +
                CONTENT +       " BLOB" +

                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
