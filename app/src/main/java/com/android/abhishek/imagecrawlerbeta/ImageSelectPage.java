package com.android.abhishek.imagecrawlerbeta;

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

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageSelectPage extends AppCompatActivity {

    private static final int REQUEST_SELECT_IMAGE = 0;

    @BindString(R.string.api_key_endpoint_1)
    String apiKeyEndPoint;
    @BindString(R.string.api_key_1)
    String apiKey;

    @BindView(R.id.imagePreviewIvAtISP)
    ImageView previewIv;
    @BindView(R.id.nFCAEttISP)
    EditText nFCEt;

    private Uri imageUri;
    private Bitmap bitmap;

    private VisionServiceClient client;

    private int pageNo = 1;
    private ArrayList<String> dataList = new ArrayList<>();

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
                    Log.e("Error oAR",e.getMessage());
                }
            }else{
                Toast.makeText(ImageSelectPage.this,"Please select an image",Toast.LENGTH_SHORT).show();
            }
        }else{
            String nFc = nFCEt.getText().toString();
            try{
                int noOfColumn = Integer.parseInt(nFc);
                //TODO start next activity with arraylist and int value
                Intent intent = new Intent(ImageSelectPage.this,FormatSetupPage.class);
                intent.putStringArrayListExtra(FormatSetupPage.DATA_LIST_PASS_INTENT,dataList);
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
                Toast.makeText(ImageSelectPage.this,"fail",Toast.LENGTH_SHORT).show();
                Log.e("Error dIB",e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);

            if (e != null) {
                Toast.makeText(ImageSelectPage.this,"fail",Toast.LENGTH_SHORT).show();
                Log.e("Error oPE",e.getMessage());
                this.e = null;
            } else {
                Gson gson = new Gson();
                OCR r = gson.fromJson(data, OCR.class);

                //  TODO store result in arraylist
                String result = "";
                for (Region reg : r.regions) {
                    for (Line line : reg.lines) {
                        for (Word word : line.words) {
                            result += word.text + " ";
                            dataList.add(word.text);
                        }
                        result += "\n";
                    }
                    result += "\n\n";
                }
                Log.d("Result : ",result);

                pageNo++;
                nFCEt.setVisibility(View.VISIBLE);
            }
            Toast.makeText(ImageSelectPage.this,"Done",Toast.LENGTH_SHORT).show();
        }
    }
}
