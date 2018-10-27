package com.android.abhishek.imagecrawlerbeta;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.abhishek.imagecrawlerbeta.adapter.ImageAdapter;
import com.android.abhishek.imagecrawlerbeta.helper.ImageHelper;
import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.LanguageCodes;
import com.microsoft.projectoxford.vision.contract.Line;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.microsoft.projectoxford.vision.contract.Region;
import com.microsoft.projectoxford.vision.contract.Word;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExtractPage extends AppCompatActivity {

    @BindView(R.id.recyclerViewAtEP)
    RecyclerView recyclerView;
    @BindView(R.id.selectALlImageIv)
    ImageView selectImage;

    @BindString(R.string.api_key_endpoint_1)
    String apiKeyEndPoint;
    @BindString(R.string.api_key_1)
    String apiKey;

    public static final String DATA_LIST_PASS_INTENT = "data_list";
    public static final String COLUMN_LIST_PASS_INTENT = "column_list";
    public static final String COLUMN_LIST_POSITION_PASS_INTENT = "column_list_position";

    private static final int REQUEST_SELECT_IMAGE = 0;
    private VisionServiceClient client;

    private ArrayList<String> dataList;
    private ArrayList<String> columnNameList = new ArrayList<>();
    private ArrayList<String> dataItemPlace = new ArrayList<>();
    private ArrayList<ArrayList<String>> dataItem = new ArrayList<>();

    private ArrayList<Bitmap> bitmapsList = new ArrayList<>();
    String imageEncoded;
    List<String> imagesEncodedList;
    private  int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extract_page);

        ButterKnife.bind(this);

       /* Intent intent = getIntent();
        if(intent == null){

        }

        dataList = intent.getStringArrayListExtra(DATA_LIST_PASS_INTENT);
        columnNameList = intent.getStringArrayListExtra(COLUMN_LIST_PASS_INTENT);
        dataItemPlace = intent.getStringArrayListExtra(COLUMN_LIST_POSITION_PASS_INTENT);

        Log.d("DATA LIST",String.valueOf(dataList));
        Log.d("COLUMN LIST",String.valueOf(columnNameList));
        Log.d("DATA ITEM PLACE",String.valueOf(dataItemPlace));*/

        if (client == null){
            client = new VisionServiceRestClient(apiKey, apiKeyEndPoint);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK && null != data) {

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                imagesEncodedList = new ArrayList<String>();
                if(data.getData()!=null){
                    Uri imageUri = data.getData();
                    Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded  = cursor.getString(columnIndex);
                    cursor.close();

                    Bitmap bitmap = ImageHelper.loadSizeLimitedBitmapFromUri(imageUri, getContentResolver());
                    bitmapsList.add(bitmap);
                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri imageUri = item.getUri();

                            Bitmap bitmap = ImageHelper.loadSizeLimitedBitmapFromUri(imageUri, getContentResolver());
                            bitmapsList.add(bitmap);

                            Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded  = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();
                        }
                        Log.d("LOG_TAG", "Selected Images" + bitmapsList.size());
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

        if(bitmapsList.size() != 0){
            selectImage.setVisibility(View.GONE);
        }else{
            selectImage.setVisibility(View.VISIBLE);
        }

        ImageAdapter imageAdapter = new ImageAdapter(bitmapsList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(imageAdapter);

        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.selectALlImageIv)
    void select(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_SELECT_IMAGE);
    }

    @OnClick(R.id.doneBtnAtEP)
    void done(){
        for(int i=0;i<bitmapsList.size();i++){
            try{
                new doRequest().execute(String.valueOf(bitmapsList.size()));
            }catch (Exception e){
                Toast.makeText(ExtractPage.this,"fail",Toast.LENGTH_SHORT).show();
                Log.e("Error : ",e.getMessage());
            }
        }
    }


    private String process(int position) throws VisionServiceException, IOException {
        Gson gson = new Gson();

        if(position == 0){
            return null;
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmapsList.get(position-1).compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        OCR ocr = this.client.recognizeText(inputStream, LanguageCodes.English, true);

        String result = gson.toJson(ocr);

        Log.d("result", result);
        return result;
    }

    private class doRequest extends AsyncTask<String, String, String> {
        private Exception e = null;

        public doRequest() {
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                return process(Integer.parseInt(args[0]));
            } catch (Exception e) {
                this.e = e;
                Log.e("Error : ",e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);

            if (e != null) {
                Toast.makeText(ExtractPage.this,"fail",Toast.LENGTH_SHORT).show();
                Log.e("Error : ",e.getMessage());
                this.e = null;
            } else {
                Gson gson = new Gson();
                OCR r = gson.fromJson(data, OCR.class);

                String result = "";
                for (Region reg : r.regions) {
                    for (Line line : reg.lines) {
                        for (Word word : line.words) {
                            result += word.text + " ";




                        }
                        result += "\n";
                    }
                    result += "\n\n";
                }
                Log.d("Result : ",result);
            }

            if(count == bitmapsList.size()){
                Toast.makeText(ExtractPage.this,"Done",Toast.LENGTH_SHORT).show();
            }else{
                count++;
            }
        }
    }
}