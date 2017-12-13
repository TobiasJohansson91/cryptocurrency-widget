package com.example.mrx.crypptocurrencywidget2;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by mrx on 2017-12-13.
 */

public class WebApiAsyncTask extends AsyncTask<Void, Void, String> {
    public static final String STRING_URL = "https://api.coinmarketcap.com/v1/ticker/";
    public static final int CONNECT_TIME = 15000;
    public static final int READ_TIME = 15000;
    public static final String REQUEST_METHOD = "GET";

    private Context context;
    private Spinner spinner;

    public WebApiAsyncTask(Context context, Spinner spinner) {
        this.context = context;
        this.spinner = spinner;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String jsonString = null;
        String input = null;
        try {
            URL url = new URL(STRING_URL);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod(REQUEST_METHOD);
            urlConnection.setReadTimeout(READ_TIME);
            urlConnection.setConnectTimeout(CONNECT_TIME);
            urlConnection.connect();

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            while ((input = bufferedReader.readLine())!= null){
                stringBuilder.append(input);
            }
            bufferedReader.close();
            urlConnection.disconnect();
            jsonString = stringBuilder.toString();

        } catch (MalformedURLException e) {
            Toast.makeText(context, "MalformedURLException", Toast.LENGTH_SHORT);
        } catch (IOException e) {
            Toast.makeText(context, "IOException", Toast.LENGTH_SHORT);
        }
        return jsonString;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        JSONArray jsonArray;
        ArrayList<String> arrayList = new ArrayList();
        if (response != null){
            try {
                jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    arrayList.add(object.getString("name"));
                }
            } catch (JSONException e) {
                Toast.makeText(context, "JSONException", Toast.LENGTH_SHORT);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrayList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
    }
}
