package com.gerardogandeaga.cyberlock.core.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gerardogandeaga.cyberlock.R;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

/**
 * @author gerardogandeaga
 */
public class DrawerFragment extends Fragment {
    private static final String TAG = "DrawerFragment";

    private Drawer mResult;

    public static DrawerFragment newInstance() {
        DrawerFragment fragment = new DrawerFragment();

        Bundle bundle = new Bundle();

        bundle.putString(TAG, "folder drawer");
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        this.mResult = new DrawerBuilder()
                .withActivity(getActivity())
                .withRootView((ViewGroup) view.findViewById(R.id.rootView))
                .withDisplayBelowStatusBar(false)
                .withSavedInstance(savedInstanceState)
                .buildForFragment();

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState = mResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
}
