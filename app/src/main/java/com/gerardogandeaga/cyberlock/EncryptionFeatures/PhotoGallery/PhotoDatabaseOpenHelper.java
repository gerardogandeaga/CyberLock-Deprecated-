package com.gerardogandeaga.cyberlock.EncryptionFeatures.PhotoGallery;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class PhotoDatabaseOpenHelper extends SQLiteOpenHelper
{
    public static final String DATABASE = "photos.db";
    public static final String TABLE = "photo";
    public static final int VERSION = 1;

    public PhotoDatabaseOpenHelper(Context context) { super(context, DATABASE, null, VERSION);}

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE photo(date INTEGER PRIMARY KEY, image KEY_IMAGE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
