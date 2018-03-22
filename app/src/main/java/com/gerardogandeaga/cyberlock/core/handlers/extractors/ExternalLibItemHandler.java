package com.gerardogandeaga.cyberlock.core.handlers.extractors;

import android.content.Context;
import android.util.Log;

import com.gerardogandeaga.cyberlock.R;
import com.gerardogandeaga.cyberlock.core.recyclerview.items.ExternalLibItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExternalLibItemHandler {
    private static final String TAG = "ExternalLibItemHandler";

    private int mLength;
    private String[] mTitles;
    private String[] mAuthors;
    private String[] mDescriptions;
    private String[] mUrls;

    public ExternalLibItemHandler(Context context) {
        this.mTitles = context.getResources().getStringArray(R.array.str_array_external_libraries);
        this.mAuthors = context.getResources().getStringArray(R.array.str_array_external_libraries_authors);
        this.mDescriptions = context.getResources().getStringArray(R.array.str_array_external_libraries_descriptions);
        this.mUrls = context.getResources().getStringArray(R.array.str_array_external_libraries_urls);

        this.mLength = (mTitles.length + mAuthors.length + mDescriptions.length + mUrls.length) / 4;

        Log.i(TAG, "ExternalLibItemHandler Titles : " + Arrays.toString(mTitles));
        Log.i(TAG, "ExternalLibItemHandler Authors : " + Arrays.toString(mAuthors));
        Log.i(TAG, "ExternalLibItemHandler Descriptions : " + Arrays.toString(mDescriptions));
        Log.i(TAG, "ExternalLibItemHandler Urls : " + Arrays.toString(mUrls));
        Log.i(TAG, "ExternalLibItemHandler Length : " + mLength);
    }

    public List<ExternalLibItem> getItems() {
        List<ExternalLibItem> externalLibItemList = new ArrayList<>();

        for (int i = 0; i < mLength; i++) {
            externalLibItemList.add(
                    new ExternalLibItem()
                            .withIdentifier((long) (i + 1))
                            .withTitle(getTitle(i))
                            .withAuthor(getAuthor(i))
                            .withDescription(getDescription(i))
                            .withUrl(getUrl(i))
            );
        }

        return externalLibItemList;
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
