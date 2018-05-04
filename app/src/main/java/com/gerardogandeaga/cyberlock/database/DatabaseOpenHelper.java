package com.gerardogandeaga.cyberlock.database;

import android.content.Context;

import com.gerardogandeaga.cyberlock.interfaces.DBFolderConstants;
import com.gerardogandeaga.cyberlock.interfaces.DBNoteConstants;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * @author gerardogandeaga
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper implements DBNoteConstants, DBFolderConstants {
    private static final String TAG = "DatabaseOpenHelper";

    public static final String DATABASE = "cyberlock.sqlite";
    private static final int VERSION = 1;

    private SQLiteDatabase mDatabase;
    private String mPassword;

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    public String getPassword() {
        return mPassword;
    }

    public void recycle() {
        close();
        this.mDatabase = null;
        this.mPassword = null;
    }

    public void update() {
        if (mDatabase == null || !mDatabase.isOpen()) {
            if (getPassword() == null || getPassword().equals("")) {
                throw new IllegalArgumentException("password null or not acceptable");
            }
            this.mDatabase = this.getWritableDatabase(getPassword());
        }
    }

    public SQLiteDatabase getWritableDatabase() {
        update();
        return mDatabase;
    }

    public SQLiteDatabase getReadableDatabase() {
        return getWritableDatabase();
    }

    @Override
    public synchronized void close() {
        super.close();
        if (mDatabase != null) {
            if (!mDatabase.isOpen()) {
                mDatabase.close();
                mDatabase = null;
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NOTES + "(" +

//              KEY                               DATA TYPE
                DBNoteConstants.DATE_MODIFIED + " INTEGER PRIMARY KEY, " +
                DBNoteConstants.DATE_CREATED +  " INTEGER, " +
                DBNoteConstants.TRASHED +       " INTEGER NOT NULL DEFAULT '0', " +
                DBNoteConstants.FOLDER +        " TEXT, " +
                DBNoteConstants.TYPE +          " TEXT, " +
                DBNoteConstants.COLOUR_TAG +    " TEXT, " +
                DBNoteConstants.LABEL +         " TEXT, " +
                DBNoteConstants.CONTENT +       " TEXT" +

                ");");

        db.execSQL("CREATE TABLE " + TABLE_FOLDERS + "(" +

//              KEY                                 DATA TYPE
                DBFolderConstants.DATE_MODIFIED + " INTEGER PRIMARY KEY, " +
                DBFolderConstants.DATE_CREATED +  " INTEGER, " +
                DBFolderConstants.COLOUR_TAG +    " TEXT, " +
                DBFolderConstants.NAME +          " TEXT" +

                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
