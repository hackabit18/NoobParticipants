package com.android.abhishek.imagecrawlerbeta;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.abhishek.imagecrawlerbeta.fragments.ColumnFeedFragment;
import com.android.abhishek.imagecrawlerbeta.fragments.DataFragment;
import com.android.abhishek.imagecrawlerbeta.fragments.DataFragment.onDataSelected;

import java.util.ArrayList;
import java.util.HashMap;

public class FormatSetupPage extends AppCompatActivity implements ColumnFeedFragment.onColumnConfirmed, onDataSelected {

    public static final String DATA_LIST_PASS_INTENT = "data_list";
    public static final String BOUNDARY_LIST_PASS_INTENT = "boundary_list";
    public static final String NO_OF_COLUMN_PASS_INTENT = "no_of_column";
    public static final String MAP_PASS_INTENT = "map_pass";

    private ArrayList<String> dataList;
    private ArrayList<String> totalBoundaryBox = new ArrayList<>();
    private HashMap<Integer,Integer> hashMap;


    private ArrayList<String> columnNameList = new ArrayList<>();
    private ArrayList<String> dataItemPlace = new ArrayList<>();
    private ArrayList<String> selectedBoundaryBox = new ArrayList<>();

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
        totalBoundaryBox =intent.getStringArrayListExtra(BOUNDARY_LIST_PASS_INTENT);
        hashMap = (HashMap<Integer, Integer>)intent.getSerializableExtra(MAP_PASS_INTENT);

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
        dataFragment.setHashMap(hashMap);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutAtFS,dataFragment).commit();
    }

    @Override
    public void onDataSelected(ArrayList<Integer> selectedItemList) {
        String item = android.text.TextUtils.join(",", selectedItemList);
        dataItemPlace.add(item);

        for(int i=0;i<selectedItemList.size();i++){
            selectedBoundaryBox.add(totalBoundaryBox.get(i));
        }

        if(totalColumnNo == 0){
            Intent intent = new Intent(FormatSetupPage.this,ExtractPage.class);
            intent.putStringArrayListExtra(ExtractPage.DATA_LIST_PASS_INTENT,dataList);
            intent.putStringArrayListExtra(ExtractPage.COLUMN_LIST_PASS_INTENT,columnNameList);
            intent.putStringArrayListExtra(ExtractPage.COLUMN_LIST_POSITION_PASS_INTENT,dataItemPlace);
            intent.putStringArrayListExtra(ExtractPage.BOUNDARY_LIST_PASS_INTENT,selectedBoundaryBox);
            intent.putExtra(ExtractPage.SORTED_MAP_POSITION_PASS_INTENT,hashMap);
            startActivity(intent);
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutAtFS,new ColumnFeedFragment()).commit();
        }
    }
}
