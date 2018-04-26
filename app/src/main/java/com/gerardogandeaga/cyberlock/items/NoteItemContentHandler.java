package com.gerardogandeaga.cyberlock.items;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gerardogandeaga.cyberlock.database.objects.Note;
import com.gerardogandeaga.cyberlock.utils.Graphics;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author gerardogandeaga on 2018-03-30.
 */
public class NoteItemContentHandler {
    private Context mContext;

    private String TYPE;

    private static int mPosition = 0;

    public NoteItemContentHandler(Context context) {
        this.mContext = context;
    }

    public List<NoteItem> getItems(List<Note> noteList) {
        // recyclerViewItem list as an array
        List<NoteItem> noteItemList = new ArrayList<>();

        // iterate through SQLite data list
        for (int i = 0; i < noteList.size(); i++) {
            Note note = noteList.get(i);

            noteItemList.add(
                    // get data package from index
                    new NoteItem()
                            .withContext(mContext)
                            .withIdentifier((long) (i))
                            .withDataObject(note)
                            .withType(note.getType())

                            .withLabel(note.getLabel())
                            .withContent(getUnbindedContent(note, note.getContent()))
                            .withDate(note.getDate())
                            .withTag(getColour(note))
                            // if type is paymentinfo
                            .withCardIcon(getCardImage(note.getContent()))
            );
        }

        return noteItemList;
    }

    public NoteItem getItem(Note note) {
        if (note == null) {
            mPosition = 0;
            return null;
        }

        return new NoteItem()
                .withContext(mContext)
                .withIdentifier((long) (mPosition++))
                .withDataObject(note)
                .withType(note.getType())

                .withLabel(note.getLabel())
                .withContent(getUnbindedContent(note, note.getContent()))
                .withDate(note.getDate())
                .withTag(getColour(note))
                // if type is paymentinfo
                .withCardIcon(getCardImage(note.getContent()));
    }

    // deconstruct content strings
    @Nullable
    private String getUnbindedContent(Note note, String content) {
        this.TYPE = note.getType();
        // return content based on rawDataPackage-type
        switch (TYPE) {
            case Note.NOTE:
                return parseNoteContent(content);
            case Note.CARD:
                return parsePaymentInfoContent(content);
            case Note.LOGIN:
                return parseLoginInfoContent(content);
            default:
                System.out.println("parsing note");
                return null;
        }
    }

    //
    @NonNull
    private String parseNoteContent(String content) {

        StringBuilder note;

        Scanner scanner = new Scanner(content);
        note = new StringBuilder(scanner.nextLine());
        while (scanner.hasNextLine()) {
            note.append("\n");
            note.append(scanner.nextLine());
        }
        scanner.close();

        return note.toString();
    }

    private String parsePaymentInfoContent(String content) {
        String holder;
        String number;

        Scanner scanner = new Scanner(content);
        holder = scanner.nextLine();
        number = scanner.nextLine();
        scanner.close();

        final String format = "%s\n%s";
        return String.format(format, holder, censorNumber(number));
    }

    private String parseLoginInfoContent(String content) {
        String url;
        String email;
        String username;

        Scanner scanner = new Scanner(content);
        url = scanner.nextLine();
        email = scanner.nextLine();
        username = scanner.nextLine();
        scanner.close();

        final String format = "%s\n%s\n%s";
        return String.format(format, url, email, username);
    }
    // ---------------------------

    private int getColour(Note note) {
        return Graphics.ColourTags.colourTagListView(mContext, note.getColourTag());
    }

    private String censorNumber(String number) {
        if (number.length() > 4) {
            StringBuilder tmp = new StringBuilder();
            for (int i = 0; i < number.length(); i++) {
                if (i == (number.length() - 4)) {
                    return tmp.append(number.substring(i, number.length())).toString();
                }
                tmp.append("*");
            }
        }
        return number;
    }

    private Drawable getCardImage(String content) {
        if (TYPE.matches(Note.CARD)) {
            String cardType;

            Scanner scanner = new Scanner(content);
            while (scanner.hasNextLine()) {
                cardType = scanner.nextLine();

                if (Graphics.CardImages.isCardType(cardType)) {
                    return Graphics.CardImages.getCardImage(cardType, .65f);
                }
            }
        }

        // don't bother and return null if not the right type
        return null;
    }
}
