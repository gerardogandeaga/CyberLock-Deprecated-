package com.gerardogandeaga.cyberlock.sqlite.gallery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PhotoDatabaseAccess
{
    private SQLiteDatabase database;
    private PhotoDatabaseOpenHelper openHelper;
    private static volatile PhotoDatabaseAccess instance;

    private PhotoDatabaseAccess(Context context)
    {
        this.openHelper = new PhotoDatabaseOpenHelper(context);
    }

    public static synchronized PhotoDatabaseAccess getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new PhotoDatabaseAccess(context);
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

    public void save(Photo photo)
    {
        ContentValues values = new ContentValues();
        values.put("date", photo.getTime());
        values.put("image", photo.getImage());

        database.insert(PhotoDatabaseOpenHelper.TABLE, null, values);
    }

    public void update(Photo photo)
    {
        ContentValues values = new ContentValues();
        values.put("date", new Date().getTime());
        values.put("image", photo.getImage());

        String date = Long.toString(photo.getTime());
        database.update(PhotoDatabaseOpenHelper.TABLE, values, "date = ?", new String[]{date});
    }

    public void delete(Photo photo)
    {
        String date = Long.toString(photo.getTime());
        database.delete(PhotoDatabaseOpenHelper.TABLE, "date = ?", new String[]{date});
    }

    public List getAllPhotos()
    {
        List photos = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * From photo ORDER BY date DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            long time = cursor.getLong(0);
            byte[] image = cursor.getBlob(1);

            photos.add(new Photo(time, image));
            cursor.moveToNext();
        }
        cursor.close();

        return photos;
    }
}
