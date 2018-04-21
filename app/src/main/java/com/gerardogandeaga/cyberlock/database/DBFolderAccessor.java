package com.gerardogandeaga.cyberlock.database;

// todo create the folder accessor

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gerardogandeaga.cyberlock.crypto.DBCrypt;
import com.gerardogandeaga.cyberlock.database.objects.Folder;
import com.gerardogandeaga.cyberlock.database.objects.Note;
import com.gerardogandeaga.cyberlock.interfaces.DBFolderConstants;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gerardogandeaga
 */
public class DBFolderAccessor implements DBFolderConstants {
    private static final String TAG = "DBFolderAccessor";

    private Context mContext;
    private SQLiteDatabase mSQLiteDatabase;
    private DBFolderOpenHelper mOpenHelper;
    private static volatile DBFolderAccessor INSTANCE;

    private static final String SQL_QUERY = "SELECT * From " + TABLE + " ORDER BY " + DATE + " DESC";

    private DBFolderAccessor(Context context) {
        this.mContext = context;
        this.mOpenHelper = new DBFolderOpenHelper(context);
    }

    // class INSTANCE manager
    public static synchronized DBFolderAccessor getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DBFolderAccessor(context);
        }
        return INSTANCE;
    }

    // database accessor states
    public void open() {
        this.mSQLiteDatabase = mOpenHelper.getWritableDatabase();
    }
    public void close() {
        if (mSQLiteDatabase != null) {
            mSQLiteDatabase.close();
            this.mSQLiteDatabase = null;
        }
    }
    public boolean isOpen() {
        return mSQLiteDatabase != null;
    }

    // database interactions / mode
    public void save(Folder folder) {
        try {
            ContentValues values = new ContentValues();

            values.put(DATE,       folder.getTime());
            values.put(COLOUR_TAG, setData(folder.getColourTag()));
            values.put(NAME,       setData(folder.getName()));

            mSQLiteDatabase.insert(TABLE, null, values);
        } catch (UnsupportedEncodingException e) {
            System.out.println("error saving folder!");
        }
    }
    public void update(Folder folder) {
        try {
            ContentValues values = new ContentValues();

            values.put(COLOUR_TAG, setData(folder.getColourTag()));
            values.put(NAME,       setData(folder.getName()));

            String date = Long.toString(folder.getTime());
            mSQLiteDatabase.update(TABLE, values, DATE + " = ?", new String[]{date});
        } catch (UnsupportedEncodingException e) {
            System.out.println("error updating folder!");
        }
    }
    public void delete(Folder folder) {
        String date = Long.toString(folder.getTime());
        mSQLiteDatabase.delete(TABLE, DATE + " = ?", new String[]{date});
    }

    public Folder getFolder(Cursor cursor) {
        if (!cursor.isAfterLast()) {
            return constructFolder(cursor);
        }
        // return null for out of bounds
        return null;
    }

    public List<Folder> getAllFolders() {
        List<Folder> folders = new ArrayList<>();
        Cursor cursor = getQuery();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            folders.add(constructFolder(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return folders;
    }

    public Folder getTopFolder() {
        Cursor cursor = getQuery();
        cursor.moveToFirst();
        return getFolder(cursor);
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

    // returns a new folder from the cursor position
    private Folder constructFolder(Cursor cursor) {
        try {
            long time =         cursor.getLong(POS_DATE);
            String colour_tag = getData(cursor.getBlob(POS_COLOUR_TAG));
            String name =       getData(cursor.getBlob(POS_NAME));
            // create new note object
            return new Folder(time, colour_tag, name);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    // this function checks if a specific piece of data exists in the database returning a boolean
    public boolean containsNote(Note note) {
        List<Folder> folders = getAllFolders();
        for (int i = 0; i < folders.size(); i++) {
            if (note.toString().equals(folders.get(i).toString())) {
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

    private Cursor getQuery() {
        return mSQLiteDatabase.rawQuery(SQL_QUERY, null);
    }
}
