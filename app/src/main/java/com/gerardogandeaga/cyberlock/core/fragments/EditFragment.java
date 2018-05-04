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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // instantiate interface
        try {
            this.mRequestResponder = (RequestResponder) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onCreate: could not cast " + TAG + " to RequestResponder class");
        }
    }

    protected abstract void compile();

    public abstract void update();

    public abstract void save();
}
