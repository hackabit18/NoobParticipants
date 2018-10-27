package com.android.abhishek.imagecrawlerbeta;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.abhishek.imagecrawlerbeta.fragments.ColumnFeedFragment;
import com.android.abhishek.imagecrawlerbeta.fragments.DataFragment;
import com.android.abhishek.imagecrawlerbeta.fragments.DataFragment.onDataSelected;
import com.android.abhishek.imagecrawlerbeta.model.DataPosition;

import java.util.ArrayList;

public class FormatSetupPage extends AppCompatActivity implements ColumnFeedFragment.onColumnConfirmed, onDataSelected {

    public static final String DATA_LIST_PASS_INTENT = "data_list";
    public static final String NO_OF_COLUMN_PASS_INTENT = "no_of_column";

    private ArrayList<String> dataList;
    private ArrayList<String> columnNameList = new ArrayList<>();
    private ArrayList<String> dataItemPlace = new ArrayList<>();

    private int totalColumnNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_format_setup_page);

        Intent intent = getIntent();
        if(intent == null){
            finish();
        }

        dataList = intent.getStringArrayListExtra(DATA_LIST_PASS_INTENT);
        try{
            totalColumnNo = Integer.parseInt(intent.getStringExtra(NO_OF_COLUMN_PASS_INTENT));
        }catch (Exception e){
            totalColumnNo = 0;
        }

        if(totalColumnNo == 0){
            startActivity(new Intent(FormatSetupPage.this,HomePage.class));
            finish();
        }else{
            getSupportFragmentManager().beginTransaction().add(R.id.frameLayoutAtFS,new ColumnFeedFragment()).commit();
        }
    }


    @Override
    public void onColumnSelected(String columnName) {
        columnNameList.add(columnName);
        totalColumnNo--;

        DataFragment dataFragment = new DataFragment();
        dataFragment.setDataList(dataList);
        dataFragment.setContext(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutAtFS,dataFragment).commit();
    }

    @Override
    public void onDataSelected(ArrayList<Integer> selectedItemList) {
        String item = android.text.TextUtils.join(",", selectedItemList);
        dataItemPlace.add(item);

        if(totalColumnNo == 0){
            Intent intent = new Intent(FormatSetupPage.this,ExtractPage.class);
            intent.putStringArrayListExtra(ExtractPage.DATA_LIST_PASS_INTENT,dataList);
            intent.putStringArrayListExtra(ExtractPage.COLUMN_LIST_PASS_INTENT,columnNameList);
            intent.putStringArrayListExtra(ExtractPage.COLUMN_LIST_POSITION_PASS_INTENT,dataItemPlace);
            startActivity(intent);
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutAtFS,new ColumnFeedFragment()).commit();
        }
    }
}
