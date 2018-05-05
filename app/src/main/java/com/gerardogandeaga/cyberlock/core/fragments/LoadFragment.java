package com.gerardogandeaga.cyberlock.core.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gerardogandeaga.cyberlock.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author gerardogandeaga
 */
public class LoadFragment extends Fragment {
    @BindView(R.id.tvTitle) TextView mTvTitle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_load, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTvTitle.setVisibility(View.GONE);
    }

    public void setTitle(String title) {
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(title);
    }
}
