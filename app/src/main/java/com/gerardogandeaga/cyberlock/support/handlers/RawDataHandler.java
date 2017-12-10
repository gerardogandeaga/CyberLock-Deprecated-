package com.gerardogandeaga.cyberlock.support.handlers;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.crypto.CryptoContent;
import com.gerardogandeaga.cyberlock.sqlite.data.RawData;

import java.util.Scanner;

public class RawDataHandler {
    private Context mContext;

    public String mLabel;
    public String mDate;
    public String mTag;
    public String mNote;

    public String mName;
    public String mNumber;
    public String mCardtype;
    public String mExpiry;
    public String mCVV;
    public Drawable mCardImage;

    public String mUrl;
    public String mEmail;
    public String mUsername;
    public String mPassword;

    public RawDataHandler(Context context, RawData rawData) {
        this.mContext = context;
        CryptoContent cc = new CryptoContent(mContext);

        // Primitive string data
        mLabel = rawData.getLabel(cc);
        mDate = rawData.getDate();
        mTag = rawData.getColourTag(cc);

        // Content parsing
        String content = rawData.getContent(cc);
        switch (rawData.getType(cc)) {
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

        mName = name;
        mNumber = number;
        mCardtype = cardType;
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
            float x = factoryIcon.getMinimumWidth();
            float y = factoryIcon.getMinimumHeight();
            float scaleFactor = 0.65f;
            int xx = (int) ((int) x - (x * scaleFactor));
            int yy = (int) ((int) y - (y * scaleFactor));

            // Apply scaling
            factoryIcon.setBounds(0, 0, xx, yy);
        }

        return factoryIcon;
    }
}
