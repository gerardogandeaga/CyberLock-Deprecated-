package com.gerardogandeaga.cyberlock.EncryptionFeatures.LoginInfo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginInfo implements Serializable
{
    private Date date;
    private String label, url, username, email, password, notes, question1, question2, answer1, answer2;
    private byte[] image;
    private boolean fullDisplayed;

    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");

    public LoginInfo()
    {
        this.date = new Date();
    }

    public LoginInfo(long time, String label, String url, String username, String email, String password, String notes, byte[] image, String question1, String question2, String answer1, String answer2)
    {
        this.date = new Date(time);
        this.label = label;
        this.url = url;
        this.username = username;
        this.email = email;
        this.password = password;
        this.notes = notes;
        this.image = image;
        this.question1 = question1;
        this.question2 = question2;
        this.answer1 = answer1;
        this.answer2 = answer2;
    }

    public String getDate()
    {
        return dateFormat.format(date);
    }
    public long getTime()
    {
        return date.getTime();
    }

    public String getLabel()
    {
        return this.label;
    }
    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getUrl()
    {
        return this.url;
    }
    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getUsername()
    {
        return this.username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getEmail()
    {
        return this.email;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return this.password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getNotes()
    {
        return this.notes;
    }
    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    public byte[] getImage() { return image; }
    public void setImage(byte[] image) { this.image = image; }
    public Drawable setImageButton(LoginInfo loginInfo)
    {
        byte[] imageByteVal = loginInfo.getImage();
        Bitmap imageBitMap = BitmapFactory.decodeByteArray(imageByteVal, 0, imageByteVal.length);

        return new BitmapDrawable(imageBitMap);
    }

    public String getQuestion1() { return question1; }
    public void setQuestion1(String question1) { this.question1 = question1; }
    public String getQuestion2() { return question2; }
    public void setQuestion2(String question2) { this.question2 = question2; }

    public String getAnswer1() { return answer1; }
    public void setAnswer1(String answer1) { this.answer1 = answer1; }
    public String getAnswer2() { return answer2; }
    public void setAnswer2(String answer2) { this.answer2 = answer2; }

    public void setFullDisplayed(boolean fullDisplayed)
    {
        this.fullDisplayed = fullDisplayed;
    }
    public boolean isFullDisplayed()
    {
        return this.fullDisplayed;
    }
}
