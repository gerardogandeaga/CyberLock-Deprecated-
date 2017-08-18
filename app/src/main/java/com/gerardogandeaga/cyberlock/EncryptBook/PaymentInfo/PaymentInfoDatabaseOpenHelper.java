package com.gerardogandeaga.cyberlock.EncryptBook.PaymentInfo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class PaymentInfoDatabaseOpenHelper extends SQLiteOpenHelper
{
    public static final String DATABASE = "paymentinfo.db";
    public static final String TABLE = "paymentinfo";
    public static final int VERSION = 1;

    public PaymentInfoDatabaseOpenHelper(Context context) { super(context, DATABASE, null, VERSION);}

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE paymentinfo(date INTEGER PRIMARY KEY, tag TEXT, cardName TEXT, cardNumber TEXT, cardExpire TEXT, cardSecCode TEXT, notes TEXT, cardType TEXT,  question1 TEXT, question2 TEXT, answer1 TEXT, answer2 TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
