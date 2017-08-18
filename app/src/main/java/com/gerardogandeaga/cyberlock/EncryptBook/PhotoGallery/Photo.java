package com.gerardogandeaga.cyberlock.EncryptBook.PhotoGallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Photo implements Serializable
{
    private byte[] mImage;
    private Date mDate;

    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");

    public Photo()
    {
        this.mDate = new Date();
    }

    public Photo(long time, byte[] image)
    {
        this.mDate = new Date(time);
        this.mImage = image;
    }

    public String getDate()
    {
        return dateFormat.format(mDate);
    }
    public long getTime()
    {
        return mDate.getTime();
    }

    public byte[] getImage() { return mImage; }
    public void setImage(byte[] image) { this.mImage = image; }

    public byte[] BitmapToByteArray(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 25, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }
    public Bitmap ByteArrayToBitmap(byte[] bytes)
    {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
    public Bitmap BitmapScale(Bitmap bitmap)
    {
        int num = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
        Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmap, 1024, num, true);

        return bitmapScaled;
    }
}
