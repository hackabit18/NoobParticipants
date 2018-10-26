package com.android.abhishek.imagecrawlerbeta.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.abhishek.imagecrawlerbeta.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectImageFragment extends Fragment {


    public SelectImageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_image, container, false);
    }

}
