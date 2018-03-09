package com.gerardogandeaga.cyberlock.core.handlers.extractors;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gerardogandeaga.cyberlock.core.recyclerview.items.RecyclerViewItem;
import com.gerardogandeaga.cyberlock.database.DataPackage;
import com.gerardogandeaga.cyberlock.utils.graphics.Graphics;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RecyclerViewItemDataHandler {
    private Context mContext;

    private String TYPE;

    private static int ID = 0;

    public RecyclerViewItemDataHandler(Context context) {
        this.mContext = context;
    }

    public List<RecyclerViewItem> getDataItems(List<DataPackage> dataPackageList) {
        // recyclerViewItem list as an array
        List<RecyclerViewItem> recyclerViewItemArrayList = new ArrayList<>();

        // iterate through SQLite data list
        for(int i = 0; i < dataPackageList.size(); i++) {
            DataPackage dataPackage = dataPackageList.get(i);

            recyclerViewItemArrayList.add(
                    // get data package from index
                    new RecyclerViewItem()
                            .withContext(mContext)
                            .withIdentifier((long) (i + 1))
                            .withRawDataPackage(dataPackage)
                            .withType(dataPackage.getType())

                            .withLabel(dataPackage.getLabel())
                            .withContent(getUnbindedContent(dataPackage, dataPackage.getContent()))
                            .withDate(dataPackage.getDate())
                            .withTag(getColour(dataPackage))
                            // if type is paymentinfo
                            .withCardIcon(getCardImage(dataPackage.getContent()))
            );
        }

        return recyclerViewItemArrayList;
    }

    public RecyclerViewItem getDataItem(DataPackage dataPackage) {
        if (dataPackage == null) {
            return null;
        }

        ID++;
        return new RecyclerViewItem()
                .withContext(mContext)
                .withIdentifier((long) (ID))
                .withRawDataPackage(dataPackage)
                .withType(dataPackage.getType())

                .withLabel(dataPackage.getLabel())
                .withContent(getUnbindedContent(dataPackage, dataPackage.getContent()))
                .withDate(dataPackage.getDate())
                .withTag(getColour(dataPackage))
                // if type is paymentinfo
                .withCardIcon(getCardImage(dataPackage.getContent()));
    }

    // deconstruct content strings
    @Nullable
    private String getUnbindedContent(DataPackage dataPackage, String content) {
        this.TYPE = dataPackage.getType();
        // return content based on rawDataPackage-type
        switch (TYPE) {
            case DataPackage.NOTE:         return dataPackage.getShortNoteText(mContext, parseNoteContent(content));
            case DataPackage.PAYMENT_INFO: return parsePaymentInfoContent(content);
            case DataPackage.LOGIN_INFO:   return parseLoginInfoContent(content);
            default: System.out.println("parsing note"); return null;
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

    private int getColour(DataPackage dataPackage) {
        return Graphics.ColourTags.colourTagListView(mContext, dataPackage.getTag());
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
        if (TYPE.matches(DataPackage.PAYMENT_INFO)) {
            String cardType;

            Scanner scanner = new Scanner(content);
            while (scanner.hasNextLine()) {
                cardType = scanner.nextLine();

                if (Graphics.CardImages.isCardType(cardType)) {
                    return Graphics.CardImages.getCardImage(mContext, cardType, .65f);
                }
            }
        }

        // don't bother and return null if not the right type
        return null;
    }
}
