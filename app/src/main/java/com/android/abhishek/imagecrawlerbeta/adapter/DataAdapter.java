package com.android.abhishek.imagecrawlerbeta.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.abhishek.imagecrawlerbeta.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.CustomDataAdapter>{

    private ArrayList<String> dataList;
    onDataSelected onDataSelectedListener;

    public interface onDataSelected{
        void onDataSelected(boolean bool,int position);
    }

    public DataAdapter(ArrayList<String> dataList) {
        this.dataList = dataList;
    }

    public void setOnDataSelected(onDataSelected onDataSelectedListener) {
        this.onDataSelectedListener = onDataSelectedListener;
    }

    @NonNull
    @Override
    public CustomDataAdapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.data_list_item,viewGroup,false);
        return new CustomDataAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomDataAdapter customDataAdapter, final int i) {
        if(dataList.size() > i){
            customDataAdapter.dataTv.setText(dataList.get(i));
        }

        customDataAdapter.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(customDataAdapter.checkBox.isChecked()){
                    customDataAdapter.checkBox.setChecked(true);
                    onDataSelectedListener.onDataSelected(true,i);
                }else{
                    customDataAdapter.checkBox.setChecked(false);
                    onDataSelectedListener.onDataSelected(false,i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class CustomDataAdapter extends RecyclerView.ViewHolder{

        @BindView(R.id.dataTvAtDLI)
        TextView dataTv;
        @BindView(R.id.checkboxAtDLI)
        CheckBox checkBox;

        public CustomDataAdapter(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
