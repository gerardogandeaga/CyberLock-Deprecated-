package com.gerardogandeaga.cyberlock.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gerardogandeaga.cyberlock.crypto.DBCrypt;
import com.gerardogandeaga.cyberlock.database.objects.NoteObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gerardogandeaga
 *
 * accessor to the Note database
 */
public class DBNoteAccessor implements DBNoteConstants {
    private static final String TAG = "DBNoteAccessor";

    private Context mContext;
    private SQLiteDatabase mSQLiteDatabase;
    private DBNoteOpenHelper mOpenHelper;
    private static volatile DBNoteAccessor INSTANCE;

    private static final String SQL_QUERY = "SELECT * From " + TABLE + " ORDER BY " + DATE + " DESC";

    private DBNoteAccessor(Context context) {
        this.mContext = context;
        this.mOpenHelper = new DBNoteOpenHelper(context);
    }

    // class INSTANCE manager
    public static synchronized DBNoteAccessor getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DBNoteAccessor(context);
        }
        return INSTANCE;
    }

    // database accessor states
    public void open() {
        Log.i(TAG, "open: Opening database...");
        mSQLiteDatabase = mOpenHelper.getWritableDatabase();
    }
    public void close() {
            Log.i(TAG, "close: Closing database...");
        if (mSQLiteDatabase != null) {
            this.mSQLiteDatabase.close();
            this.mSQLiteDatabase = null;
        }
    }
    public boolean isOpen() {
        return mSQLiteDatabase != null;
    }

    // database interactions / mods
    public void save(NoteObject noteObject) {
        try {
            ContentValues values = new ContentValues();

            values.put(DATE,       noteObject.getTime());
            values.put(FOLDER,     setData(noteObject.getFolder()));
            values.put(TYPE,       setData(noteObject.getType()));
            values.put(COLOUR_TAG, setData(noteObject.getTag()));
            values.put(LABEL,      setData(noteObject.getLabel()));
            values.put(CONTENT,    setData(noteObject.getContent()));

            mSQLiteDatabase.insert(DBNoteOpenHelper.TABLE, null, values);
        } catch (UnsupportedEncodingException e) {
            System.out.println("error saving note!");
        }
    }
    public void update(NoteObject noteObject) {
        try {
            ContentValues values = new ContentValues();

            values.put(DATE,       new Date().getTime());
            values.put(FOLDER,     setData(noteObject.getFolder()));
            values.put(TYPE,       setData(noteObject.getType()));
            values.put(COLOUR_TAG, setData(noteObject.getTag()));
            values.put(LABEL,      setData(noteObject.getLabel()));
            values.put(CONTENT,    setData(noteObject.getContent()));

            String date = Long.toString(noteObject.getTime());
            mSQLiteDatabase.update(DBNoteOpenHelper.TABLE, values, "date = ?", new String[]{date});
        } catch (UnsupportedEncodingException e) {
            System.out.println("error updating note!");
        }
    }
    public void delete(NoteObject noteObject) {
        String date = Long.toString(noteObject.getTime());
        mSQLiteDatabase.delete(DBNoteOpenHelper.TABLE, "date = ?", new String[]{date});
    }

    // data packages getters
    // list
    public List<NoteObject> getAllDataPackages() {
        List<NoteObject> noteObjects = new ArrayList<>();
        Cursor cursor = getQuery();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            noteObjects.add(constructDataPackage(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return noteObjects;
    }
    public int size() {
        int size = 0;
        Cursor cursor = getQuery();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            size++;
            cursor.moveToNext();
        }
        return size;
    }
    // single position
    public NoteObject getDataPackage(Cursor cursor) {
        if (!cursor.isAfterLast()) {
            return constructDataPackage(cursor);
        }

        // return null for out of bounds
        return null;
    }

    // returns a new data package from the cursor position
    private NoteObject constructDataPackage(Cursor cursor) {
        try {
            long time =         cursor.getLong(POS_DATE);
            String folder =     getData(cursor.getBlob(POS_FOLDER));
            String type =       getData(cursor.getBlob(POS_TYPE));
            String colour_tag = getData(cursor.getBlob(POS_COLOUR_TAG));
            String label =      getData(cursor.getBlob(POS_LABEL));
            String content =    getData(cursor.getBlob(POS_CONTENT));
            // create new note object
            return new NoteObject(time, folder, type, colour_tag, label, content);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    // this function checks if a specific piece of data exists in the database returning a boolean
    public boolean containsData(NoteObject noteObject) {
        List<NoteObject> noteObjects = getAllDataPackages();
        for (int i = 0; i < noteObjects.size(); i++) {
            System.out.println(noteObject);
            System.out.println(noteObjects.get(i));
            if (noteObject.toString().equals(noteObjects.get(i).toString())) {
                System.out.println(noteObject.toString());
                System.out.println(noteObjects.get(i).toString());

                return false;
            }
        }
        return true;
    }

    // data encryption
    // when putting data into the database
    private byte[] setData(String data) throws UnsupportedEncodingException {
        return DBCrypt.encrypt(mContext, data);
    }
    // when pulling data from the database and defining the dataPackage object
    private String getData(byte[] data) throws UnsupportedEncodingException {
        return DBCrypt.decrypt(mContext, data);
    }

    public Cursor getQuery() {
        return mSQLiteDatabase.rawQuery(SQL_QUERY, null);
    }
}
