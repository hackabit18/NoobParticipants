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
import com.android.abhishek.imagecrawlerbeta.listener.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DataFragment extends Fragment implements DataAdapter.onDataSelected {

    @BindView(R.id.dataRvAtFD)
    RecyclerView recyclerView;

    private ArrayList<String> dataList;
    private ArrayList<Integer> selectedItem = new ArrayList<>();
    private HashMap<Integer,Integer> hashMap;


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

    public void setHashMap(HashMap<Integer, Integer> hashMap) {
        this.hashMap = hashMap;
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

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        selectedItem.add(hashMap.get(position));
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }

    @OnClick(R.id.nextBtnAtDF)
    void next(){
        onDataSelectedListener.onDataSelected(selectedItem);
    }


    @Override
    public void onDataSelected(boolean bool,int position) {
       /* if(bool){
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
        }*/
    }
}
