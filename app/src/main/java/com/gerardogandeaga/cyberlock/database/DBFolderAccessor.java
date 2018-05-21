package com.gerardogandeaga.cyberlock.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.gerardogandeaga.cyberlock.App;
import com.gerardogandeaga.cyberlock.database.objects.Folder;
import com.gerardogandeaga.cyberlock.interfaces.DBFolderConstants;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gerardogandeaga
 */
public class DBFolderAccessor implements DBFolderConstants {
    private static final String TAG = "DBFolderAccessor";

    private DatabaseOpenHelper mOpenHelper;
    private static volatile DBFolderAccessor INSTANCE;

    private static final String SQL_QUERY = "SELECT * From " + TABLE_FOLDERS + " ORDER BY " + DATE_CREATED + " DESC";

    private DBFolderAccessor() {
        this.mOpenHelper = App.getDatabase();
    }

    // class INSTANCE manager
    public static synchronized DBFolderAccessor getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DBFolderAccessor();
        }
        return INSTANCE;
    }

    // database interactions / mode
    public void save(Folder folder) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DATE_MODIFIED, folder.getTimeCreated());
        values.put(DATE_CREATED,  folder.getTimeCreated());
        values.put(COLOUR_TAG,    folder.getColourTag());
        values.put(NAME,          folder.getName());

        db.beginTransaction();
        db.insert(TABLE_FOLDERS, null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void update(Folder folder) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DATE_MODIFIED, new Date().getTime());
        values.put(COLOUR_TAG,    folder.getColourTag());
        values.put(NAME,          folder.getName());

        String date = Long.toString(folder.getTimeCreated());

        db.beginTransaction();
        db.update(TABLE_FOLDERS, values, DATE_MODIFIED + " = ?", new String[]{date});
        db.setTransactionSuccessful();
        db.endTransaction();
    }
    public void delete(Folder folder) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String date = Long.toString(folder.getTimeCreated());

        db.beginTransaction();
        db.delete(TABLE_FOLDERS, DATE_MODIFIED + " = ?", new String[]{date});
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private Folder getFolder(Cursor cursor) {
        if (!cursor.isAfterLast()) {
            return constructFolder(cursor);
        }
        // return null for out of bounds
        return null;
    }

    public List<Folder> getAllFolders() {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        List<Folder> folders = new ArrayList<>();
        Cursor cursor = getQuery(db);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            folders.add(constructFolder(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return folders;
    }

    public Folder getTopFolder() {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        Cursor cursor = getQuery(db);
        cursor.moveToFirst();

        return getFolder(cursor);
    }

    public int size() {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int size = 0;
        Cursor cursor = getQuery(db);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            size++;
            cursor.moveToNext();
        }

        return size;
    }

    // returns a new folder from the cursor position
    private Folder constructFolder(Cursor cursor) {
        long modded =       cursor.getLong(POS_DATE_MODIFIED);
        long created =      cursor.getLong(POS_DATE_CREATED);
        String colour_tag = cursor.getString(POS_COLOUR_TAG);
        String name =       cursor.getString(POS_NAME);
        // create new folder object
        return new Folder(modded, created, colour_tag, name);
    }

    // this function checks if a specific piece of data exists in the database returning a boolean
    public boolean containsFolder(Folder folder) {
        List<Folder> folders = getAllFolders();
        for (int i = 0; i < folders.size(); i++) {
            if (folder.equals(folders.get(i))) {
                return true;
            }
        }
        return false;
    }

    private Cursor getQuery(SQLiteDatabase db) {
        return db.rawQuery(SQL_QUERY, null);
    }
}
