package com.android.abhishek.imagecrawlerbeta.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonParser {

    public ArrayList<String> parseJsonData(String json){

        if (json != null) {
            ArrayList<String> boundaryBox = new ArrayList<>();
            try {
                JSONObject jsonObj = new JSONObject(json);
                JSONArray jsonArray = jsonObj.getJSONArray("regions");
                for(int i=0;i<jsonArray.length();i++){
                    JSONArray linesArray = jsonArray.getJSONArray(i);
                }



            } catch (final JSONException e) {
                return null;
            }

            return boundaryBox;
        }else{
            return null;
        }
    }
}
