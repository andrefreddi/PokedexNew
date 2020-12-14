package com.example.apppokedex;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ParseApplications {
    private static final String TAG = "ParseApplications";

    private ArrayList<FeedEntry> applications;

    public ParseApplications() {
        applications = new ArrayList<>();
    }

    public ArrayList<FeedEntry> getApplications() {
        return applications;
    }

    public boolean parse(String jsonResponse) {
        boolean status = true;
        FeedEntry entry;
        String num, name, img, height, weight;
        JSONArray jsonArray;
        JSONObject oneObject = null;

        try {

            JSONObject jObject = new JSONObject(jsonResponse);

            JSONArray jArray = jObject.getJSONArray("pokemon");

            for (int i=0; i < jArray.length(); i++)
            {
                try {

                    entry = new FeedEntry();
                    List<String> type = new ArrayList<>();

                    oneObject = jArray.getJSONObject(i);
                    num = oneObject.getString("num");
                    name = oneObject.getString("name");
                    img = oneObject.getString("img");
                    height = oneObject.getString("height");
                    weight = oneObject.getString("weight");
                    jsonArray = oneObject.getJSONArray("type");
                    for (int a=0; a<jsonArray.length(); a++) {
                        type.add(jsonArray.getString(a));
                   }
                    entry.setNum(num);
                    entry.setName(name);
                    entry.setImg(img);
                    entry.setHeight(height);
                    entry.setWeight(weight);
                    entry.setType(type);

                    applications.add(entry);

                } catch (JSONException e) {

                    e.printStackTrace();

                }
            }

        } catch (Exception ex) {
            Log.e(TAG, "parse: Erro ao fazer parse: " + ex.getMessage());
            status = false;
        }

        return status;
    }
}