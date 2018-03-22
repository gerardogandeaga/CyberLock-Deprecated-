package com.gerardogandeaga.cyberlock.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gerardogandeaga.cyberlock.crypto.database.DBCrypt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBAccess {
    private static final String TAG = "DBAccess";
    private Context mContext;
    private SQLiteDatabase mSQLiteDatabase;
    private DBOpenHelper mOpenHelper;
    private static volatile DBAccess INSTANCE;

    private static final String SQL_QUERY = "SELECT * From data ORDER BY date DESC";

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

    // data packages getters
    // list
    public List<DataPackage> getAllDataPackages() {
        List<DataPackage> dataPackages = new ArrayList<>();
        Cursor cursor = getQuery();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            dataPackages.add(constructDataPackage(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return dataPackages;
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
    public DataPackage getDataPackage(Cursor cursor) {
        if (!cursor.isAfterLast()) {
            return constructDataPackage(cursor);
        }

        // return null for out of bounds
        return null;
    }

    // returns a new data package from the cursor position
    private DataPackage constructDataPackage(Cursor cursor) {
        long time = cursor.getLong(0);
        String type =    getData(cursor.getString(1));
        String tag =     getData(cursor.getString(2));
        String label =   getData(cursor.getString(3));
        String content = getData(cursor.getString(4));
        return new DataPackage(time, type, tag, label, content);
    }

    // this function checks if a specific piece of data exists in the database returning a boolean
    public boolean containsData(DataPackage dataPackage) {
        List<DataPackage> dataPackages = getAllDataPackages();
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

    // data encryption
    // when pulling data from the database and defining the dataPackage object
    private String getData(String data) {
        return DBCrypt.decrypt(mContext, data);
    }
    // when putting data into the database
    private String setData(String data) {
        return DBCrypt.encrypt(mContext, data);
    }

    public Cursor getQuery() {
        return mSQLiteDatabase.rawQuery(SQL_QUERY, null);
    }
}
