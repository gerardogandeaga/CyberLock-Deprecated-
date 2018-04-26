package com.gerardogandeaga.cyberlock.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gerardogandeaga.cyberlock.interfaces.DBFolderConstants;

/**
 * @author gerardogandeaga
 */
public class DBFolderOpenHelper extends SQLiteOpenHelper implements DBFolderConstants {
    private static final String DB = TABLE + ".db";
    private static final int VERSION = 1;

    DBFolderOpenHelper(Context context) {
        super(context, DB, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + "(" +

//              KEY               DATA TYPE
                DATE_CREATED +  " INTEGER, " +
                DATE_MODIFIED + " INTEGER PRIMARY KEY, " +
                COLOUR_TAG +    " BLOB, " +
                NAME +          " BLOB" +

                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
