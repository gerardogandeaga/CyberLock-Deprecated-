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

    public void save(RawDataPackage rawDataPackage) {
        ContentValues values = new ContentValues();
        values.put("date", rawDataPackage.getTime());
        values.put("type", rawDataPackage.getType());
        values.put("colour", rawDataPackage.getColourTag());
        values.put("label", rawDataPackage.getLabel());
        values.put("content", rawDataPackage.getContent());
        mSQLiteDatabase.insert(MasterDatabaseOpenHelper.TABLE, null, values);
    }
    public void update(RawDataPackage rawDataPackage) {
        ContentValues values = new ContentValues();
        values.put("date", new Date().getTime());
        values.put("type", rawDataPackage.getType());
        values.put("colour", rawDataPackage.getColourTag());
        values.put("label", rawDataPackage.getLabel());
        values.put("content", rawDataPackage.getContent());
        String date = Long.toString(rawDataPackage.getTime());
        mSQLiteDatabase.update(MasterDatabaseOpenHelper.TABLE, values, "date = ?", new String[]{date});
    }
    public void delete(RawDataPackage rawDataPackage) {
        String date = Long.toString(rawDataPackage.getTime());
        mSQLiteDatabase.delete(MasterDatabaseOpenHelper.TABLE, "date = ?", new String[]{date});
    }

    public List<RawDataPackage> getAllData() {
        List<RawDataPackage> data = new ArrayList<>();
        Cursor cursor = mSQLiteDatabase.rawQuery("SELECT * From data ORDER BY date DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long time = cursor.getLong(0);
            String type = cursor.getString(1);
            String colour = cursor.getString(2);
            String label = cursor.getString(3);
            String content = cursor.getString(4);
            data.add(new RawDataPackage(time, type, colour, label, content));
            cursor.moveToNext();
        }
        cursor.close();

        return data;
    }

    public boolean containsData(RawDataPackage rawDataPackage) {
        List<RawDataPackage> rawDataPackages = getAllData();
        for (int i = 0; i < rawDataPackages.size(); i++) {
            System.out.println(rawDataPackage);
            System.out.println(rawDataPackages.get(i));
            if (rawDataPackage.toString().equals(rawDataPackages.get(i).toString())) {
                System.out.println(rawDataPackage.toString());
                System.out.println(rawDataPackages.get(i).toString());
                return false;
            }
        }
        return true;
    }
}
