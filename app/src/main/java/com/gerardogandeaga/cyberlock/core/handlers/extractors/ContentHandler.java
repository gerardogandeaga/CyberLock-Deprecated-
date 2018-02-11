package com.gerardogandeaga.cyberlock.core.handlers.extractors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.database.DataPackage;
import com.gerardogandeaga.cyberlock.utils.math.Scaling;

import java.util.Scanner;

public class ContentHandler {
    private Context mContext;

    public String mLabel;
    public String mDate;
    public String mTag;

    public String mNote;

    public String mHolder;
    public String mNumber;
    public String mCardType;
    public String mExpiry;
    public String mCVV;
    public Drawable mCardImage;

    public String mUrl;
    public String mEmail;
    public String mUsername;
    public String mPassword;

    public ContentHandler(Context context, DataPackage dataPackage) {
        this.mContext = context;

        // Primitive string data
        mLabel = dataPackage.getLabel();
        mDate = dataPackage.getDate();
        mTag = dataPackage.getTag();

        // Content parsing
        String content = dataPackage.getContent();
        switch (dataPackage.getType()) {
            case "TYPE_NOTE":        setNoteContent(content); break;
            case "TYPE_PAYMENTINFO": setPaymentInfoContent(content); break;
            case "TYPE_LOGININFO":   setLoginInfoContent(content); break;
        }
    }

    // String setters
    private void setNoteContent(String content) {
        StringBuilder note = new StringBuilder();
        //
        Scanner scanner = new Scanner(content);

        try {
            note.append(scanner.nextLine());
            while (scanner.hasNextLine()) {
                note.append("\n");
                note.append(scanner.nextLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        scanner.close();

        mNote = note.toString();
    }
    private void setPaymentInfoContent(String content) {
        String name = "";
        String number = "";
        String cardType = "";
        String expiry = "";
        String cvv = "";
        StringBuilder note = new StringBuilder();
        //
        Scanner scanner = new Scanner(content);

        try {
            name = scanner.nextLine();
            number = scanner.nextLine();
            cardType = scanner.nextLine();
            expiry = scanner.nextLine();
            cvv = scanner.nextLine();
            note.append(scanner.nextLine());
            while (scanner.hasNextLine()) {
                note.append("\n");
                note.append(scanner.nextLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        scanner.close();

        mHolder = name;
        mNumber = number;
        mCardType = cardType;
        mExpiry = expiry;
        mCVV = cvv;
        mNote = note.toString();
        mCardImage = getCardImage(content);
    }
    private void setLoginInfoContent(String content) {
        String url = "";
        String email = "";
        String username = "";
        String password = "";
        StringBuilder note = new StringBuilder();
        //
        Scanner scanner = new Scanner(content);

        try {
            url = scanner.nextLine();
            email = scanner.nextLine();
            username = scanner.nextLine();
            password = scanner.nextLine();
            note.append(scanner.nextLine());
            while (scanner.hasNextLine()) {
                note.append("\n");
                note.append(scanner.nextLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        scanner.close();

        mUrl = url;
        mEmail = email;
        mUsername = username;
        mPassword = password;
        mNote = note.toString();
    }

    // Set card image
    private Drawable getCardImage(String content) {
        String cardType;
        Drawable factoryIcon = null;

        Scanner scanner = new Scanner(content);
        while (scanner.hasNextLine()) {
            cardType = scanner.nextLine();
            switch (cardType) {
                case ("Visa"):             factoryIcon = mContext.getResources().getDrawable(R.drawable.card_visa); break;
                case ("Master Card"):      factoryIcon = mContext.getResources().getDrawable(R.drawable.card_mastercard); break;
                case ("American Express"): factoryIcon = mContext.getResources().getDrawable(R.drawable.card_americanexpress); break;
                case ("Discover"):         factoryIcon = mContext.getResources().getDrawable(R.drawable.card_discover); break;
                case ("Other"):            factoryIcon = mContext.getResources().getDrawable(R.drawable.card_default); break;
            }
        }

        // Icon scaling
        if (factoryIcon != null) {
            int x = Scaling.dpToPx(mContext, 47);
            int y = Scaling.dpToPx(mContext, 32);

            Bitmap bitmap = ((BitmapDrawable) factoryIcon).getBitmap();

            return new BitmapDrawable(mContext.getResources(), Bitmap.createScaledBitmap(bitmap, x, y, true));
        }
        return null;
    }
}
