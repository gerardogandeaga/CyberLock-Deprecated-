package com.gerardogandeaga.cyberlock.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author gerardogandeaga
 */
public class DBFolderOpenHelper extends SQLiteOpenHelper {
    private static final String DB = "folders.db";
    static final String TABLE = "folders";
    private static final int VERSION = 1;

    // values
    static final String DATE = "date";
    static final String FOLDER_TYPE = "type";
    static final String NAME = "name";
    static final String SIZE = "size";

    DBFolderOpenHelper(Context context) {
        super(context, DB, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + "(" +

//              KEY             DATA TYPE
                DATE +        " INTEGER PRIMARY KEY, " +
                FOLDER_TYPE + " BLOB, " +
                NAME +        " BLOB, " +
                SIZE +        " BLOB" +

                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
