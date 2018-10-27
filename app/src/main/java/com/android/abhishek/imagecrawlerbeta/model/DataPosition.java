package com.android.abhishek.imagecrawlerbeta.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class DataPosition implements Parcelable{

    private ArrayList<Integer> arrayList;

    public DataPosition(ArrayList<Integer> arrayList) {
        this.arrayList = arrayList;
    }

    protected DataPosition(Parcel in) {

    }

    public static final Creator<DataPosition> CREATOR = new Creator<DataPosition>() {
        @Override
        public DataPosition createFromParcel(Parcel in) {
            return new DataPosition(in);
        }

        @Override
        public DataPosition[] newArray(int size) {
            return new DataPosition[size];
        }
    };

    public ArrayList<Integer> getArrayList() {
        return arrayList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        List<Integer> list = new ArrayList<>();
        list.addAll(arrayList);
        parcel.writeList(list);
    }
}
