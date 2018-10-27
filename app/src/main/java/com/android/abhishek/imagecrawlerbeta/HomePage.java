package com.android.abhishek.imagecrawlerbeta;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomePage extends AppCompatActivity {

    @BindView(R.id.startBtnAtHP)
    Button startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.startBtnAtHP)
    void startExtracting(){
        startActivity(new Intent(HomePage.this,ImageSelectPage.class));
    }
}
