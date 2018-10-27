package com.android.abhishek.imagecrawlerbeta.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.abhishek.imagecrawlerbeta.ExtractPage;
import com.android.abhishek.imagecrawlerbeta.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ColumnFeedFragment extends Fragment {

    @BindView(R.id.columnNameEtAtCF)
    EditText columnNameEt;

    onColumnConfirmed onColumnConfirmedListener;

    public interface onColumnConfirmed{
        void onColumnSelected(String columnName);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            onColumnConfirmedListener = (onColumnConfirmed) context;
        }catch (Exception e){
            throw new ClassCastException(context.toString() + " not implemented onColumnConfirmedListener");
        }
    }

    public ColumnFeedFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_column_feed, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @OnClick(R.id.nextBtnAtCF)
    void next(){
        String columnName = columnNameEt.getText().toString();
        if(columnName != null){
            onColumnConfirmedListener.onColumnSelected(columnName);
        }
    }
}
