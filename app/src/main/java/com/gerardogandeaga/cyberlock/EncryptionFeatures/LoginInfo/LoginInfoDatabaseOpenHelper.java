package com.gerardogandeaga.cyberlock.EncryptionFeatures.LoginInfo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class LoginInfoDatabaseOpenHelper extends SQLiteOpenHelper
{
    public static final String DATABASE = "logininfo.db";
    public static final String TABLE = "logininfo";
    public static final int VERSION = 1;

    public LoginInfoDatabaseOpenHelper(Context context) { super(context, DATABASE, null, VERSION);}

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE logininfo(date INTEGER PRIMARY KEY, tag TEXT, url TEXT, username TEXT, email TEXT, password TEXT, notes TEXT, image KEY_IMAGE, question1 TEXT, question2 TEXT, answer1 TEXT, answer2 TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
