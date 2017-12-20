package com.example.mrx.crypptocurrencywidget2;

import android.appwidget.AppWidgetManager;
import android.os.AsyncTask;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by mrx on 2017-12-14.
 */

public class GetCoinWebApiAsyncTask extends AsyncTask<Void, Void, String> {
    private final String URL = "https://api.coinmarketcap.com/v1/ticker/";
    private final int CONNECT_TIME = 15000;
    private final int READ_TIME = 5000;
    private final String REQUEST_METHOD = "GET";

    private String coinRequest;
    private RemoteViews views;
    private double boughtPrice;
    private int appWidgetId;
    private AppWidgetManager appWidgetManager;

    public GetCoinWebApiAsyncTask(RemoteViews views, String coin, Double boughtPrice, int appWidgetId, AppWidgetManager appWidgetManager) {
        this.coinRequest = coin + "/";
        this.views = views;
        this.boughtPrice = boughtPrice;
        this.appWidgetId = appWidgetId;
        this.appWidgetManager = appWidgetManager;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String response = null;
        URL url;

        try {
            url = new URL(URL + coinRequest);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod(REQUEST_METHOD);
            urlConnection.setConnectTimeout(CONNECT_TIME);
            urlConnection.setReadTimeout(READ_TIME);
            urlConnection.connect();

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));
            String input;
            StringBuilder stringBuilder = new StringBuilder();
            while ((input = bufferedReader.readLine()) != null) {
                stringBuilder.append(input);
            }
            bufferedReader.close();
            urlConnection.disconnect();
            response = stringBuilder.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String jasonString) {
        super.onPostExecute(jasonString);
        String value = "0";
        if (jasonString != null) {
            try {
                JSONArray jsonArray = new JSONArray(jasonString);
                JSONObject jsonObject =jsonArray.getJSONObject(0);
                value = "" + jsonObject.getDouble("price_usd");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            double percent = Double.parseDouble(value);
            percent = percent / boughtPrice * 100;
            views.setTextViewText(R.id.textView2, value + " $");
            views.setTextViewText(R.id.textView3, "" + percent + "%");
            appWidgetManager.updateAppWidget(appWidgetId,views);
        }
    }
}
