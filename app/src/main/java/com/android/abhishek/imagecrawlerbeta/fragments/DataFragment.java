package com.android.abhishek.imagecrawlerbeta.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.abhishek.imagecrawlerbeta.R;
import com.android.abhishek.imagecrawlerbeta.adapter.DataAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DataFragment extends Fragment implements DataAdapter.onDataSelected {

    @BindView(R.id.dataRvAtFD)
    RecyclerView recyclerView;

    private ArrayList<String> dataList;
    private ArrayList<Integer> selectedItem = new ArrayList<>();

    private Context context;

    onDataSelected onDataSelectedListener;

    public interface onDataSelected{
        void onDataSelected(ArrayList<Integer> selectedItemList);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            onDataSelectedListener = (onDataSelected) context;
        }catch (Exception e){
            throw new ClassCastException(context.toString() + " not implemented onColumnConfirmedListener");
        }
    }

    public DataFragment() {

    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setDataList(ArrayList<String> dataList) {
        this.dataList = dataList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DataAdapter dataAdapter = new DataAdapter(dataList);
        dataAdapter.setOnDataSelected(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerView.setAdapter(dataAdapter);
    }

    @OnClick(R.id.nextBtnAtDF)
    void next(){
        onDataSelectedListener.onDataSelected(selectedItem);
    }


    @Override
    public void onDataSelected(boolean bool,int position) {
        if(bool){
            try{
                if(!selectedItem.contains(new Integer(position))){
                    selectedItem.add(position);
                }
            }catch (Exception e){
                Log.e("Error DF : ",e.getMessage());
            }
        }else{
            try{
                selectedItem.remove(new Integer(position));
                Log.e("Size : ",String.valueOf(selectedItem.size()));
            }catch (Exception e){
                Log.e("Error DF : ",e.getMessage());
            }
        }
    }
}
