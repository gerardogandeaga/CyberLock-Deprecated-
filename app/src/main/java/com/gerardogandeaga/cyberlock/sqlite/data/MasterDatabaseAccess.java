package com.gerardogandeaga.cyberlock.sqlite.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MasterDatabaseAccess {
    private SQLiteDatabase mSQLiteDatabase;
    private MasterDatabaseOpenHelper mOpenHelper;
    private static volatile MasterDatabaseAccess instance;

    private MasterDatabaseAccess(Context context) {
        mOpenHelper = new MasterDatabaseOpenHelper(context);
    }

    public static synchronized MasterDatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new MasterDatabaseAccess(context);
        }

        return instance;
    }

    public void open() {
        mSQLiteDatabase = mOpenHelper.getWritableDatabase();
    }
    public void close() {
        if (mSQLiteDatabase != null) {
            this.mSQLiteDatabase.close();
        }
    }

    public void save(RawData rawData) {
        ContentValues values = new ContentValues();
        values.put("date", rawData.getTime());
        values.put("type", rawData.getType());
        values.put("colour", rawData.getColourTag());
        values.put("label", rawData.getLabel());
        values.put("content", rawData.getContent());
        mSQLiteDatabase.insert(MasterDatabaseOpenHelper.TABLE, null, values);
    }
    public void update(RawData rawData) {
        ContentValues values = new ContentValues();
        values.put("date", new Date().getTime());
        values.put("type", rawData.getType());
        values.put("colour", rawData.getColourTag());
        values.put("label", rawData.getLabel());
        values.put("content", rawData.getContent());
        String date = Long.toString(rawData.getTime());
        mSQLiteDatabase.update(MasterDatabaseOpenHelper.TABLE, values, "date = ?", new String[]{date});
    }

    public void delete(RawData rawData) {
        String date = Long.toString(rawData.getTime());
        mSQLiteDatabase.delete(MasterDatabaseOpenHelper.TABLE, "date = ?", new String[]{date});
    }

    public List<RawData> getAllData() {
        List<RawData> data = new ArrayList<>();
        Cursor cursor = mSQLiteDatabase.rawQuery("SELECT * From data ORDER BY date DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long time = cursor.getLong(0);
            String type = cursor.getString(1);
            String colour = cursor.getString(2);
            String label = cursor.getString(3);
            String content = cursor.getString(4);
            data.add(new RawData(time, type, colour, label, content));
            cursor.moveToNext();
        }
        cursor.close();

        return data;
    }
}
