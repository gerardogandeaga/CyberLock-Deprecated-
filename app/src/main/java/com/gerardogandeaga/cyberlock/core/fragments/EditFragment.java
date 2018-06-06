package com.gerardogandeaga.cyberlock.core.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gerardogandeaga.cyberlock.interfaces.RequestResponder;

/**
 * @author gerardogandeaga
 */
public abstract class EditFragment extends Fragment {
    private static final String TAG = "EditFragment";

    protected RequestResponder mRequestResponder;

    private boolean mIsReadOnly;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // instantiate interface
        try {
            this.mRequestResponder = (RequestResponder) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onCreate: could not cast " + TAG + " to RequestResponder class");
        }

        this.mIsReadOnly = false;
    }

    protected abstract void compile();

    public abstract void update();

    public abstract void save();

    public abstract void toggleViewMode();

    protected void setReadOnly(boolean isReadOnly) {
        this.mIsReadOnly = isReadOnly;
    }

    public boolean isReadOnly() {
        return mIsReadOnly;
    }
}
