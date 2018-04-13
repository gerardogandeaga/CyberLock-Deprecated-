package com.gerardogandeaga.cyberlock.helpers.content;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.gerardogandeaga.cyberlock.database.objects.Note;
import com.gerardogandeaga.cyberlock.utils.Graphics;

import java.util.Scanner;

/**
 * @author gerardogandeaga
 */
// todo create getter methods for the note variables
public class NoteContentHandler {
    private Context mContext;

    public String mLabel;
    public String mDate;
    public String mTag;

    public String mNotes;

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

    public NoteContentHandler(Context context, Note note) {
        this.mContext = context;

        // Primitive string data
        mLabel = note.getLabel();
        mDate = note.getDate();
        mTag = note.getColourTag();

        // Content parsing
        String content = note.getContent();
        switch (note.getType()) {
            case Note.NOTE:         setNoteContent(content); break;
            case Note.CARD: setPaymentInfoContent(content); break;
            case Note.LOGIN:   setLoginInfoContent(content); break;
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

        mNotes = note.toString();
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
        mNotes = note.toString();
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
        mNotes = note.toString();
    }

    // Set card image
    private Drawable getCardImage(String content) {
        String cardType;

        Scanner scanner = new Scanner(content);
        while (scanner.hasNextLine()) {
            cardType = scanner.nextLine();

            if (Graphics.CardImages.isCardType(cardType)) {
                return Graphics.CardImages.getCardImage(mContext, cardType, 47, 32);
            }
        }

        return null;
    }
}
