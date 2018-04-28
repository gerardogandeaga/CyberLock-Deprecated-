package com.gerardogandeaga.cyberlock.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gerardogandeaga.cyberlock.crypto.DBCrypt;
import com.gerardogandeaga.cyberlock.database.objects.Note;
import com.gerardogandeaga.cyberlock.interfaces.DBNoteConstants;

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

    private static final String SQL_QUERY = "SELECT * From " + TABLE + " ORDER BY " + DATE_MODIFIED + " DESC";

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
        this.mSQLiteDatabase = mOpenHelper.getWritableDatabase();
    }
    public void close() {
        Log.i(TAG, "close: Closing database...");
        if (mSQLiteDatabase != null) {
            mSQLiteDatabase.close();
            this.mSQLiteDatabase = null;
        }
    }
    public boolean isOpen() {
        return mSQLiteDatabase != null;
    }

    // database interactions / mods
    public void save(Note note) {
        try {
            ContentValues values = new ContentValues();

            values.put(DATE_MODIFIED, note.getTimeCreated());
            values.put(DATE_CREATED,  note.getTimeCreated());
            values.put(TRASHED,       note.isTrashed());
            values.put(FOLDER,        setData(note.getFolder()));
            values.put(TYPE,          setData(note.getType()));
            values.put(COLOUR_TAG,    setData(note.getColourTag()));
            values.put(LABEL,         setData(note.getLabel()));
            values.put(CONTENT,       setData(note.getContent()));

            mSQLiteDatabase.insert(TABLE, null, values);
        } catch (UnsupportedEncodingException e) {
            System.out.println("error saving note!");
        }
    }
    public void update(Note note) {
        try {
            ContentValues values = new ContentValues();

            values.put(DATE_MODIFIED, new Date().getTime());
            values.put(DATE_CREATED,  note.getTimeCreated());
            values.put(TRASHED,       note.isTrashed());
            values.put(FOLDER,        setData(note.getFolder()));
            values.put(TYPE,          setData(note.getType()));
            values.put(COLOUR_TAG,    setData(note.getColourTag()));
            values.put(LABEL,         setData(note.getLabel()));
            values.put(CONTENT,       setData(note.getContent()));

            String date = Long.toString(note.getTimeModified());
            mSQLiteDatabase.update(TABLE, values, DATE_MODIFIED + " = ?", new String[]{date});
        } catch (UnsupportedEncodingException e) {
            System.out.println("error updating note!");
        }
    }
    public void delete(Note note) {
        String date = Long.toString(note.getTimeModified());
        mSQLiteDatabase.delete(TABLE, DATE_MODIFIED + " = ?", new String[]{date});
    }

    /**
     * @param cursor position in db
     * @return regular unfiltered note
     */
    public Note getNote(Cursor cursor) {
        if (!cursor.isAfterLast()) {
            return constructNote(cursor);
        }

        // return null for out of bounds
        return null;
    }

    /**
     * @param cursor position in db
     * @param folder folder filter
     * @return note that pertains to a particular folder
     */
    public Note getNote(Cursor cursor, String folder) {
        if (!cursor.isAfterLast()) {
            Note note = constructNote(cursor);
            if (note != null) {
                if (folder.equals(note.getFolder())) {
                    return note;
                } else {
                    return new Note();
                }
            }
        }
        return null;
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        Cursor cursor = getQuery();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            notes.add(getNote(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return notes;
    }

    /**
     * @param folder filter
     * @return list of notes in a particular folder
     */
    public List<Note> getAllNotes(String folder) {
        List<Note> notes = new ArrayList<>();
        Cursor cursor = getQuery();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            notes.add(getNote(cursor, folder));
            cursor.moveToNext();
        }
        cursor.close();

        return notes;
    }

    public List<Note> getTrashedNotes() {
        List<Note> trashedNotes = new ArrayList<>();
        Cursor cursor = getQuery();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Note note = constructNote(cursor);
            if (note.isTrashed()) {
                trashedNotes.add(note);
            }
        }
        return trashedNotes;
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

    // returns a new data package from the cursor position
    private Note constructNote(Cursor cursor) {
        try {
            long modded =       cursor.getLong(POS_DATE_MODIFIED);
            long created =      cursor.getLong(POS_DATE_CREATED);
            boolean isTrashed = cursor.getShort(POS_TRASHED) == 1;
            String folder =     getData(cursor.getBlob(POS_FOLDER));
            String type =       getData(cursor.getBlob(POS_TYPE));
            String colourTag =  getData(cursor.getBlob(POS_COLOUR_TAG));
            String label =      getData(cursor.getBlob(POS_LABEL));
            String content =    getData(cursor.getBlob(POS_CONTENT));
            // create new note object
            return new Note(modded, created, isTrashed, folder, type, colourTag, label, content);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * this function checks if a specific piece of data exists in the
     * database returning a boolean
     */
    public boolean containsNote(Note note) {
        List<Note> notes = getAllNotes();
        for (int i = 0; i < notes.size(); i++) {
            if (note.toString().equals(notes.get(i).toString())) {
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
