package com.android.abhishek.imagecrawlerbeta.adapter;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.abhishek.imagecrawlerbeta.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.CustomImageAdapter>{

    private ArrayList<Bitmap> imageList;

    public ImageAdapter(ArrayList<Bitmap> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public CustomImageAdapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_item,viewGroup,false);
        return new CustomImageAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomImageAdapter customImageAdapter, int i) {
        if(i<imageList.size()){
            customImageAdapter.imageView.setImageBitmap(imageList.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class CustomImageAdapter extends RecyclerView.ViewHolder{

        @BindView(R.id.imagePreviewIvAtID)
        ImageView imageView;

        public CustomImageAdapter(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
