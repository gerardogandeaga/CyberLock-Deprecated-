package com.gerardogandeaga.cyberlock.EncryptionFeatures.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MasterDatabaseAccess
{
    private SQLiteDatabase mSQLiteDatabase;
    private MasterDatabaseOpenHelper mOpenHelper;
    private static volatile MasterDatabaseAccess instance;

    private MasterDatabaseAccess(Context context)
    {
        this.mOpenHelper = new MasterDatabaseOpenHelper(context);
    }

    public static synchronized MasterDatabaseAccess getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new MasterDatabaseAccess(context);
        }

        return instance;
    }

    public void open()
    {
        this.mSQLiteDatabase = mOpenHelper.getWritableDatabase();
    }

    public void close()
    {
        if (mSQLiteDatabase != null)
        {
            this.mSQLiteDatabase.close();
        }
    }

    public void save(Data data)
    {
        ContentValues values = new ContentValues();
        values.put("date", data.getTime());
        values.put("type", data.getType());
        values.put("label", data.getLabel());
        values.put("content", data.getContent());
        mSQLiteDatabase.insert(MasterDatabaseOpenHelper.TABLE, null, values);
    }

    public void update(Data data)
    {
        ContentValues values = new ContentValues();
        values.put("date", new Date().getTime());
        values.put("type", data.getType());
        values.put("label", data.getLabel());
        values.put("content", data.getContent());
        String date = Long.toString(data.getTime());
        mSQLiteDatabase.update(MasterDatabaseOpenHelper.TABLE, values, "date = ?", new String[]{date});
    }

    public void delete(Data data)
    {
        String date = Long.toString(data.getTime());
        mSQLiteDatabase.delete(MasterDatabaseOpenHelper.TABLE, "date = ?", new String[]{date});
    }

    public List getAllData()
    {
        List data = new ArrayList<>();
        Cursor cursor = mSQLiteDatabase.rawQuery("SELECT * From data ORDER BY date DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            long time = cursor.getLong(0);
            String type = cursor.getString(1);
            String label = cursor.getString(2);
            String content = cursor.getString(3);
            data.add(new Data(time, type, label, content));
            cursor.moveToNext();
        }
        cursor.close();

        return data;
    }
}
