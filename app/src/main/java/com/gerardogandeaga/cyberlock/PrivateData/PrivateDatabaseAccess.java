package com.gerardogandeaga.cyberlock.PrivateData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrivateDatabaseAccess
{
    private static volatile PrivateDatabaseAccess instance;
    private SQLiteDatabase mSQLiteDatabase;
    private PrivateDatabaseOpenHelper mOpenHelper;

    private PrivateDatabaseAccess(Context context) {
        this.mOpenHelper = new PrivateDatabaseOpenHelper(context);
    }
    public static synchronized PrivateDatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new PrivateDatabaseAccess(context);
        }

        return instance;
    }

    public void open() {
        this.mSQLiteDatabase = mOpenHelper.getWritableDatabase();
    }
    public void close() {
        if (mSQLiteDatabase != null) {
            this.mSQLiteDatabase.close();
        }
    }

    public void save(PrivateData data) {
        ContentValues values = new ContentValues();
        values.put("lastlogin", data.getTime());
        values.put("passcode", data.getType());
        values.put("cryptkey", data.getCryptKey());
        mSQLiteDatabase.insert(PrivateDatabaseOpenHelper.TABLE, null, values);
    }
    public void update(PrivateData data) {
        ContentValues values = new ContentValues();
        values.put("lastlogin", new Date().getTime());
        values.put("passcode", data.getType());
        values.put("cryptkey", data.getCryptKey());
        String date = Long.toString(data.getTime());
        mSQLiteDatabase.update(PrivateDatabaseOpenHelper.TABLE, values, "date = ?", new String[]{date});
    }
    public void delete(PrivateData data) {
        String date = Long.toString(data.getTime());
        mSQLiteDatabase.delete(PrivateDatabaseOpenHelper.TABLE, "date = ?", new String[]{date});
    }

    public List getAllData() {
        List data = new ArrayList<>();
        Cursor cursor = mSQLiteDatabase.rawQuery("SELECT * From data ORDER BY date DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long lastlogin = cursor.getLong(0);
            String passcode = cursor.getString(1);
            String cryptkey = cursor.getString(2);
            data.add(new PrivateData(lastlogin, passcode, cryptkey));
            cursor.moveToNext();
        }
        cursor.close();

        return data;
    }
}