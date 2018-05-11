package com.gerardogandeaga.cyberlock.items;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.gerardogandeaga.cyberlock.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author gerardogandeaga on 2018-03-30.
 */
public class ExternalLibItemContentHandler {
    private static final String TAG = "ExternalLibItemContentHandler";

    private int mLength;
    private String[] mTitles;
    private String[] mAuthors;
    private String[] mDescriptions;
    private String[] mUrls;

    @SuppressLint("LongLogTag")
    public ExternalLibItemContentHandler(Context context) {
        this.mTitles = context.getResources().getStringArray(R.array.str_array_external_libraries);
        this.mAuthors = context.getResources().getStringArray(R.array.str_array_external_libraries_authors);
        this.mDescriptions = context.getResources().getStringArray(R.array.str_array_external_libraries_descriptions);
        this.mUrls = context.getResources().getStringArray(R.array.str_array_external_libraries_urls);

        this.mLength = (mTitles.length + mAuthors.length + mDescriptions.length + mUrls.length) / 4;

        Log.i(TAG, "ExternalLibItemContentHandler Titles : " + Arrays.toString(mTitles));
        Log.i(TAG, "ExternalLibItemContentHandler Authors : " + Arrays.toString(mAuthors));
        Log.i(TAG, "ExternalLibItemContentHandler Descriptions : " + Arrays.toString(mDescriptions));
        Log.i(TAG, "ExternalLibItemContentHandler Urls : " + Arrays.toString(mUrls));
        Log.i(TAG, "ExternalLibItemContentHandler Length : " + mLength);
    }

    public List<LibItem> getItems() {
        List<LibItem> libItemList = new ArrayList<>();

        for (int i = 0; i < mLength; i++) {
            libItemList.add(
                    new LibItem()
                            .withIdentifier((long) (i + 1))
                            .withTitle(getTitle(i))
                            .withAuthor(getAuthor(i))
                            .withDescription(getDescription(i))
                            .withUrl(getUrl(i))
            );
        }

        return libItemList;
    }

    private String getTitle(int i) {
        return mTitles[i];
    }
    private String getAuthor(int i) {
        return mAuthors[i];
    }
    private String getDescription(int i) {
        return mDescriptions[i];
    }
    private String getUrl(int i) {
        return mUrls[i];
    }
}
