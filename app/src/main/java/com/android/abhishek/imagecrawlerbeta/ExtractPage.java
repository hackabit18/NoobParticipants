package com.android.abhishek.imagecrawlerbeta;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class ExtractPage extends AppCompatActivity {

    public static final String DATA_LIST_PASS_INTENT = "data_list";
    public static final String COLUMN_LIST_PASS_INTENT = "column_list";
    public static final String COLUMN_LIST_POSITION_PASS_INTENT = "column_list_position";

    private ArrayList<String> dataList;
    private ArrayList<String> columnNameList = new ArrayList<>();
    private ArrayList<String> dataItemPlace = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extract_page);

        Intent intent = getIntent();
        if(intent == null){
            finish();
        }

        dataList = intent.getStringArrayListExtra(DATA_LIST_PASS_INTENT);
        columnNameList = intent.getStringArrayListExtra(COLUMN_LIST_PASS_INTENT);
        dataItemPlace = intent.getStringArrayListExtra(COLUMN_LIST_POSITION_PASS_INTENT);

        Log.d("DATA LIST",String.valueOf(dataList));
        Log.d("COLUMN LIST",String.valueOf(columnNameList));
        Log.d("DATA ITEM PLACE",String.valueOf(dataItemPlace));


    }
}
