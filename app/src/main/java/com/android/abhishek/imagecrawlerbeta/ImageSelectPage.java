package com.android.abhishek.imagecrawlerbeta;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageSelectPage extends AppCompatActivity {

    private static final int REQUEST_SELECT_IMAGE = 0;
    private ProgressDialog mProgressDialog;

    @BindString(R.string.api_key_endpoint_1)
    String apiKeyEndPoint;
    @BindString(R.string.api_key_1)
    String apiKey;

    @BindView(R.id.imagePreviewIvAtISP)
    ImageView previewIv;
    @BindView(R.id.nFCAEttISP)
    EditText nFCEt;
    @BindView(R.id.noOfColumnRlAtISP)
    RelativeLayout relativeLayout;

    private Uri imageUri;
    private Bitmap bitmap;

    private VisionServiceClient client;

    private int pageNo = 1;
    private ArrayList<String> dataList = new ArrayList<>();
    private ArrayList<String> boundaryBox = new ArrayList<>();

    private HashMap<String,Integer> sampleMp = new HashMap<>();
    private HashMap<Integer,Integer> sortedMp = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select_page);
        ButterKnife.bind(this);

        if (client == null){
            client = new VisionServiceRestClient(apiKey, apiKeyEndPoint);
        }

    }

    @OnClick(R.id.imagePreviewIvAtISP)
    void startGallery(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_SELECT_IMAGE);
    }

    @OnClick(R.id.imageSelectDoneAtISP)
    void next(){
        if(pageNo == 1){
            if (bitmap != null) {
                try{
                    new doRequest().execute();
                }catch (Exception e){
                    Toast.makeText(ImageSelectPage.this,"fail",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(ImageSelectPage.this,"Please select an image",Toast.LENGTH_SHORT).show();
            }
            showProgressDialog();
        }else{
            String nFc = nFCEt.getText().toString();
            try{
                int noOfColumn = Integer.parseInt(nFc);
                Intent intent = new Intent(ImageSelectPage.this,FormatSetupPage.class);
                intent.putStringArrayListExtra(FormatSetupPage.DATA_LIST_PASS_INTENT,dataList);
                intent.putStringArrayListExtra(FormatSetupPage.BOUNDARY_LIST_PASS_INTENT,boundaryBox);
                intent.putExtra(FormatSetupPage.MAP_PASS_INTENT,sortedMp);
                intent.putExtra(FormatSetupPage.NO_OF_COLUMN_PASS_INTENT,String.valueOf(noOfColumn));
                startActivity(intent);
            }catch (Exception e){
                Toast.makeText(ImageSelectPage.this,"Please select an integer value",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && REQUEST_SELECT_IMAGE == requestCode){
            imageUri = data.getData();
            bitmap = ImageHelper.loadSizeLimitedBitmapFromUri(imageUri, getContentResolver());
            if (bitmap != null) {
                previewIv.setImageBitmap(bitmap);
            }
        }
    }

    private String process() throws VisionServiceException, IOException {
        Gson gson = new Gson();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        OCR ocr = this.client.recognizeText(inputStream, LanguageCodes.English, true);

        String result = gson.toJson(ocr);

        Log.d("result", result);
        return result;
    }

    private class doRequest extends AsyncTask<Void, String, String> {
        private Exception e = null;

        public doRequest() {
        }

        @Override
        protected String doInBackground(Void... args) {
            try {
                return process();
            } catch (Exception e) {
                this.e = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);

            if (e != null) {
                Toast.makeText(ImageSelectPage.this,"fail",Toast.LENGTH_SHORT).show();
                this.e = null;
            } else {
                Gson gson = new Gson();
                OCR r = gson.fromJson(data, OCR.class);

                String result = "";
                for (Region reg : r.regions) {
                    for (Line line : reg.lines) {
                        for (Word word : line.words) {
                            result += word.text + " ";
                            dataList.add(word.text);
                            String res = "";
                            for(int i=0;i<word.boundingBox.length();i++){
                                if(word.boundingBox.charAt(i) != ','){
                                    res += word.boundingBox.charAt(i);
                                }
                            }

                            if(word.boundingBox.length() == 3){
                                res += "0";
                            }else if(word.boundingBox.length() == 2){
                                res += "00";
                            }else if(word.boundingBox.length() == 1){
                                res += "000";
                            }

                            boundaryBox.add(res);
                        }
                        result += "\n";
                    }
                    result += "\n\n";
                }
                Log.d("Result : ",result);

                pageNo++;
                relativeLayout.setVisibility(View.VISIBLE);
                previewIv.setVisibility(View.GONE);
                sortData();
            }
            Toast.makeText(ImageSelectPage.this,"Done",Toast.LENGTH_SHORT).show();
            hideProgressDialog();
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void sortData(){
        for(int i=0;i<dataList.size();i++){
            sampleMp.put(boundaryBox.get(i),i);
        }
        Collections.sort(boundaryBox.subList(1, boundaryBox.size()));

        for(int i=0;i<dataList.size();i++){
            sortedMp.put(i,sampleMp.get(boundaryBox.get(i)));
        }
    }
}
