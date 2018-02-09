package com.gerardogandeaga.cyberlock.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gerardogandeaga.cyberlock.crypto.database.DBCrypt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBAccess {
    private Context mContext;
    private SQLiteDatabase mSQLiteDatabase;
    private DBOpenHelper mOpenHelper;
    private static volatile DBAccess INSTANCE;

    private DBAccess(Context context) {
        this.mContext = context;
        this.mOpenHelper = new DBOpenHelper(context);
    }

    // class INSTANCE manager
    public static synchronized DBAccess getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DBAccess(context);
        }
        return INSTANCE;
    }

    // database accessor states
    public void open() {
        mSQLiteDatabase = mOpenHelper.getWritableDatabase();
    }
    public void close() {
        if (mSQLiteDatabase != null) {
            this.mSQLiteDatabase.close();
        }
    }

    // database interactions / mods
    public void save(DataPackage dataPackage) {
        ContentValues values = new ContentValues();
        values.put("date", dataPackage.getTime());
        values.put("type", setData(dataPackage.getType()));
        values.put("colour", setData(dataPackage.getTag()));
        values.put("label", setData(dataPackage.getLabel()));
        values.put("content", setData(dataPackage.getContent()));
        mSQLiteDatabase.insert(DBOpenHelper.TABLE, null, values);
    }
    public void update(DataPackage dataPackage) {
        ContentValues values = new ContentValues();
        values.put("date", new Date().getTime());
        values.put("type", setData(dataPackage.getType()));
        values.put("colour", setData(dataPackage.getTag()));
        values.put("label", setData(dataPackage.getLabel()));
        values.put("content", setData(dataPackage.getContent()));

        String date = Long.toString(dataPackage.getTime());
        mSQLiteDatabase.update(DBOpenHelper.TABLE, values, "date = ?", new String[]{date});
    }
    public void delete(DataPackage dataPackage) {
        String date = Long.toString(dataPackage.getTime());
        mSQLiteDatabase.delete(DBOpenHelper.TABLE, "date = ?", new String[]{date});
    }

    // get all data packages
    public List<DataPackage> getAllData() {
        List<DataPackage> data = new ArrayList<>();
        Cursor cursor = mSQLiteDatabase.rawQuery("SELECT * From data ORDER BY date DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long time = cursor.getLong(0);
            String type =    getData(cursor.getString(1));
            String colour =  getData(cursor.getString(2));
            String label =   getData(cursor.getString(3));
            String content = getData(cursor.getString(4));
            data.add(new DataPackage(time, type, colour, label, content));
            cursor.moveToNext();
        }
        cursor.close();

        return data;
    }

    // when pulling data from the database and defining the dataPackage object
    private String getData(String data) {
        return DBCrypt.decrypt(mContext, data);
    }
    // when putting data into the database
    private String setData(String data) {
        return DBCrypt.encrypt(mContext, data);
    }

    // this function checks if a specific piece of data exists in the database returning a boolean
    public boolean containsData(DataPackage dataPackage) {
        List<DataPackage> dataPackages = getAllData();
        for (int i = 0; i < dataPackages.size(); i++) {
            System.out.println(dataPackage);
            System.out.println(dataPackages.get(i));
            if (dataPackage.toString().equals(dataPackages.get(i).toString())) {
                System.out.println(dataPackage.toString());
                System.out.println(dataPackages.get(i).toString());

                return false;
            }
        }
        return true;
    }
}
