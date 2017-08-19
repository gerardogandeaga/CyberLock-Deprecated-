package com.gerardogandeaga.cyberlock.EncryptionFeatures.LoginInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoginInfoDatabaseAccess
{
    private SQLiteDatabase database;
    private LoginInfoDatabaseOpenHelper openHelper;
    private static volatile LoginInfoDatabaseAccess instance;

    private LoginInfoDatabaseAccess(Context context)
    {
        this.openHelper = new LoginInfoDatabaseOpenHelper(context);
    }

    public static synchronized LoginInfoDatabaseAccess getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new LoginInfoDatabaseAccess(context);
        }

        return instance;
    }

    public void open() { this.database = openHelper.getWritableDatabase(); }

    public void close()
    {
        if (database != null)
        {
            this.database.close();
        }
    }

    public void save(LoginInfo loginInfo)
    {
        ContentValues values = new ContentValues();
        values.put("date", loginInfo.getTime());
        values.put("tag", loginInfo.getLabel());
        values.put("url", loginInfo.getUrl());
        values.put("username", loginInfo.getUsername());
        values.put("email", loginInfo.getEmail());
        values.put("password", loginInfo.getPassword());
        values.put("notes", loginInfo.getNotes());
        values.put("image", loginInfo.getImage());
        values.put("question1", loginInfo.getQuestion1());
        values.put("question2", loginInfo.getQuestion2());
        values.put("answer1", loginInfo.getAnswer1());
        values.put("answer2", loginInfo.getAnswer2());

        database.insert(LoginInfoDatabaseOpenHelper.TABLE, null, values);
    }

    public void update(LoginInfo loginInfo)
    {
        ContentValues values = new ContentValues();
        values.put("date", new Date().getTime());
        values.put("tag", loginInfo.getLabel());
        values.put("url", loginInfo.getUrl());
        values.put("username", loginInfo.getUsername());
        values.put("email", loginInfo.getEmail());
        values.put("password", loginInfo.getPassword());
        values.put("notes", loginInfo.getNotes());
        values.put("image", loginInfo.getImage());
        values.put("question1", loginInfo.getQuestion1());
        values.put("question2", loginInfo.getQuestion2());
        values.put("answer1", loginInfo.getAnswer1());
        values.put("answer2", loginInfo.getAnswer2());

        String date = Long.toString(loginInfo.getTime());
        database.update(LoginInfoDatabaseOpenHelper.TABLE, values, "date = ?", new String[]{date});
    }

    public void delete(LoginInfo loginInfo)
    {
        String date = Long.toString(loginInfo.getTime());
        database.delete(LoginInfoDatabaseOpenHelper.TABLE, "date = ?", new String[]{date});
    }

    public List getAllLoginInfos()
    {
        List loginInfos = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * From logininfo ORDER BY date DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            long time = cursor.getLong(0);
            String tag = cursor.getString(1);
            String url = cursor.getString(2);
            String username = cursor.getString(3);
            String email = cursor.getString(4);
            String password = cursor.getString(5);
            String notes = cursor.getString(6);
            byte[] image = cursor.getBlob(7);
            String question1 = cursor.getString(8);
            String question2 = cursor.getString(9);
            String answer1 = cursor.getString(10);
            String answer2 = cursor.getString(11);

            loginInfos.add(new LoginInfo(time, tag,url, username, email, password, notes, image, question1, question2, answer1, answer2));
            cursor.moveToNext();
        }
        cursor.close();

        return loginInfos;
    }
}
