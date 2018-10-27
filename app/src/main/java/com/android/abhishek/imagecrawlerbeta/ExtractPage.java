package com.android.abhishek.imagecrawlerbeta;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExtractPage extends AppCompatActivity {

    public static final String DATA_LIST_PASS_INTENT = "data_list";
    public static final String BOUNDARY_LIST_PASS_INTENT = "boundary_list";
    public static final String COLUMN_LIST_PASS_INTENT = "column_list";
    public static final String COLUMN_LIST_POSITION_PASS_INTENT = "column_list_position";
    public static final String SORTED_MAP_POSITION_PASS_INTENT = "sorted_map_position";

    @BindView(R.id.recyclerViewAtEP)
    RecyclerView recyclerView;
    @BindView(R.id.selectALlImageIv)
    ImageView selectImage;

    @BindString(R.string.api_key_endpoint_1)
    String apiKeyEndPoint;
    @BindString(R.string.api_key_1)
    String apiKey;

    private static final int REQUEST_SELECT_IMAGE = 0;
    private VisionServiceClient client;

    private ArrayList<String> dataList;
    private ArrayList<String> columnNameList = new ArrayList<>();
    private ArrayList<String> concatDataItemPlace = new ArrayList<>();
    private ArrayList<Bitmap> bitmapsList = new ArrayList<>();
    private ArrayList<ArrayList<String>> dataExtracted = new ArrayList<>();
    private ArrayList<ArrayList<String>> boundaryBox = new ArrayList<>();
    private ArrayList<String> selectedBoundaryBox = new ArrayList<>();
    private HashMap<Integer,Integer> sortedMp = new HashMap<>();
    private HashMap<Integer,Integer> mpp = new HashMap<>() ;
    private List<String> imagesEncodedList;
    private  HashMap<String,Integer> mp = new HashMap<>() ;
    private String imageEncoded;
    private int count = 0;
    private String mySheet = "";
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extract_page);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent == null) {

        }

        dataList = intent.getStringArrayListExtra(DATA_LIST_PASS_INTENT);
        columnNameList = intent.getStringArrayListExtra(COLUMN_LIST_PASS_INTENT);
        concatDataItemPlace = intent.getStringArrayListExtra(COLUMN_LIST_POSITION_PASS_INTENT);
        selectedBoundaryBox = intent.getStringArrayListExtra(BOUNDARY_LIST_PASS_INTENT);
        sortedMp =  (HashMap<Integer, Integer>)intent.getSerializableExtra(SORTED_MAP_POSITION_PASS_INTENT);


        if (client == null) {
            client = new VisionServiceRestClient(apiKey, apiKeyEndPoint);
        }

        addColumnTitle();
       // fillSheet(dataList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK && null != data) {

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<String>();
                if (data.getData() != null) {
                    Uri imageUri = data.getData();
                    Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
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
                            imageEncoded = cursor.getString(columnIndex);
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

        if (bitmapsList.size() != 0) {
            selectImage.setVisibility(View.GONE);
        } else {
            selectImage.setVisibility(View.VISIBLE);
        }

        ImageAdapter imageAdapter = new ImageAdapter(bitmapsList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(imageAdapter);

        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.selectALlImageIv)
    void select() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_SELECT_IMAGE);
    }

    @OnClick(R.id.doneBtnAtEP)
    void done() {
        try{
            new doRequest().execute(String.valueOf(0));
        }catch (Exception e){

        }
    }

    private String process(int position) throws VisionServiceException, IOException {
        Gson gson = new Gson();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmapsList.get(position).compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        OCR ocr = this.client.recognizeText(inputStream, LanguageCodes.English, true);

        String result = gson.toJson(ocr);

        Log.d("result", result);
        return result;
    }

    private class doRequest extends AsyncTask<String, String, String> {
        private Exception e = null;

        @Override
        protected String doInBackground(String... args) {
            try {
                return process(Integer.parseInt(args[0]));
            } catch (Exception e) {
                this.e = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);

            if (e != null) {
                Toast.makeText(ExtractPage.this,"fail",Toast.LENGTH_SHORT).show();
                this.e = null;
            } else {
                Gson gson = new Gson();
                OCR r = gson.fromJson(data, OCR.class);

                String result = "";
                String str = "";
                ArrayList<String> arrayList = new ArrayList<>();
                ArrayList<String> arrayList1 = new ArrayList<>();
                for (Region reg : r.regions) {
                    for (Line line : reg.lines) {
                        for (Word word : line.words) {
                            result += word.text + " ";
                            arrayList.add(word.text);

                            str += word.boundingBox+"\n";
                            arrayList1.add(word.boundingBox);
                        }
                        result += "\n";
                    }
                    result += "\n\n";
                }
                Log.v("str : ",str);
                dataExtracted.add(arrayList);
                boundaryBox.add(arrayList1);
                count++;

                if(count == bitmapsList.size()){
                    // Terminate
                    for(int i=0;i<dataExtracted.size();i++){
                        mp.clear();
                        for(int j = 0 ; j< boundaryBox.get(i).size() ; j++){
                            mp.put(boundaryBox.get(i).get(j),j) ;
                        }
                        Collections.sort(boundaryBox.get(i).subList(1, boundaryBox.get(i).size()));
                        for(int j = 0 ; j< boundaryBox.get(i).size() ; j++){
                            mpp.put(j,mp.get(boundaryBox.get(i).get(j)));
                        }
                        fillSheet(dataExtracted.get(i));
                    }

                    try {
                        File root = new File(Environment.getExternalStorageDirectory(), "HackABit");
                        if (!root.exists()) {
                            root.mkdirs();
                        }
                        File gpxfile = new File(root, "doc1.csv");
                        FileWriter writer = new FileWriter(gpxfile);
                        writer.append(mySheet);
                        writer.flush();
                        writer.close();
                        Toast.makeText(ExtractPage.this, "Saved", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else{
                    new doRequest().execute(String.valueOf(count));
                }
            }
        }
    }

    private void addColumnTitle(){
        for(int i=0;i<columnNameList.size();i++){
            mySheet += columnNameList.get(i);
            if(i == columnNameList.size()-1){
                mySheet += "\n";
            }else{
                mySheet += ",";
            }
        }
    }

    private void fillSheet(ArrayList<String> A ){
         String toadd = "" ;
         int columns = columnNameList.size() ;
         for(int i = 0 ; i< columns ; i++){
            String str = concatDataItemPlace.get(i) ;
            int id = 0 ;
            int j = 0 ;
            while(j<str.length()){
                if(str.charAt(j) == ','){
                    //send string at id in sorted form(sorted by boundary value)
                    int hl = mpp.get(id);
                    toadd += A.get(mpp.get(id))+" " ;
                    j++ ; id = 0 ;
                    continue;
                }
                id = id*10+str.charAt(j)-'0' ;
                j++ ;
            }
            toadd += A.get(mpp.get(id))+',' ;
         }
         toadd  += '\n' ;
         mySheet += toadd;
    }
}