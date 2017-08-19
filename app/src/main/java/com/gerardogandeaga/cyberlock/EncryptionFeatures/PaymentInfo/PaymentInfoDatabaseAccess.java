package com.gerardogandeaga.cyberlock.EncryptionFeatures.PaymentInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentInfoDatabaseAccess
{
    private SQLiteDatabase database;
    private PaymentInfoDatabaseOpenHelper openHelper;
    private static volatile PaymentInfoDatabaseAccess instance;

    private PaymentInfoDatabaseAccess(Context context)
    {
        this.openHelper = new PaymentInfoDatabaseOpenHelper(context);
    }

    public static synchronized PaymentInfoDatabaseAccess getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new PaymentInfoDatabaseAccess(context);
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

    public void save(PaymentInfo paymentInfo)
    {
        ContentValues values = new ContentValues();
        values.put("date", paymentInfo.getTime());
        values.put("tag", paymentInfo.getLabel());
        values.put("cardName", paymentInfo.getCardName());
        values.put("cardNumber", paymentInfo.getCardNumber());
        values.put("cardExpire", paymentInfo.getCardExpire());
        values.put("cardSecCode", paymentInfo.getCardSecCode());
        values.put("notes", paymentInfo.getNotes());
        values.put("cardType", paymentInfo.getCardType());
        values.put("question1", paymentInfo.getQuestion1());
        values.put("question2", paymentInfo.getQuestion2());
        values.put("answer1", paymentInfo.getAnswer1());
        values.put("answer2", paymentInfo.getAnswer2());

        database.insert(PaymentInfoDatabaseOpenHelper.TABLE, null, values);
    }

    public void update(PaymentInfo paymentInfo)
    {
        ContentValues values = new ContentValues();
        values.put("date", new Date().getTime());
        values.put("tag", paymentInfo.getLabel());
        values.put("cardName", paymentInfo.getCardName());
        values.put("cardNumber", paymentInfo.getCardNumber());
        values.put("cardExpire", paymentInfo.getCardExpire());
        values.put("cardSecCode", paymentInfo.getCardSecCode());
        values.put("notes", paymentInfo.getNotes());
        values.put("cardType", paymentInfo.getCardType());
        String date = Long.toString(paymentInfo.getTime());
        values.put("question1", paymentInfo.getQuestion1());
        values.put("question2", paymentInfo.getQuestion2());
        values.put("answer1", paymentInfo.getAnswer1());
        values.put("answer2", paymentInfo.getAnswer2());

        database.update(PaymentInfoDatabaseOpenHelper.TABLE, values, "date = ?", new String[]{date});
    }

    public void delete(PaymentInfo paymentInfo)
    {
        String date = Long.toString(paymentInfo.getTime());
        database.delete(PaymentInfoDatabaseOpenHelper.TABLE, "date = ?", new String[]{date});
    }

    public List getAllPaymentInfos()
    {
        List paymentInfos = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * From paymentinfo ORDER BY date DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            long time = cursor.getLong(0);
            String tag = cursor.getString(1);
            String cardName = cursor.getString(2);
            String cardNumber = cursor.getString(3);
            String cardExpire = cursor.getString(4);
            String cardSecCode = cursor.getString(5);
            String notes = cursor.getString(6);
            String cardType = cursor.getString(7);
            String question1 = cursor.getString(8);
            String question2 = cursor.getString(9);
            String answer1 = cursor.getString(10);
            String answer2 = cursor.getString(11);

            paymentInfos.add(new PaymentInfo(time, tag,cardName, cardNumber, cardExpire, cardSecCode, notes, cardType, question1, question2, answer1, answer2));
            cursor.moveToNext();
        }
        cursor.close();

        return paymentInfos;
    }
}
