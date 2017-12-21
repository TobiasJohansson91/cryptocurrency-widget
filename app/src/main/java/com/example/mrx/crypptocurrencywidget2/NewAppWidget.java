package com.example.mrx.crypptocurrencywidget2;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link NewAppWidgetConfigureActivity NewAppWidgetConfigureActivity}
 */
public class NewAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        String widgetText = NewAppWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(NewAppWidgetConfigureActivity.PREFS_NAME, 0);
        double boughtPrice = Double.parseDouble(sharedPreferences.getString(NewAppWidgetConfigureActivity.PREF_PREFIX_KEY + appWidgetId, "0"));

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.textView, widgetText);
        views.setImageViewResource(R.id.imageView, android.R.drawable.ic_delete);
        View view = new View(context);

        ArrayList<String> stringArray = new ArrayList(Arrays.asList(view.getResources().getStringArray(R.array.coins)));
        int[] drawableArray = {R.drawable.bitcoin, R.drawable.cardano, R.drawable.dash, R.drawable.eos,
        R.drawable.ethereum, R.drawable.iota, R.drawable.litecoin, R.drawable.monero, R.drawable.nem,
        R.drawable.neo, R.drawable.omisego, R.drawable.ripple, R.drawable.stellar};

        for (int i = 0; i < stringArray.size(); i++) {
            if (stringArray.get(i).equals(widgetText.toLowerCase())){
                views.setImageViewResource(R.id.imageView, drawableArray[i]);
            }
        }

        Intent intent = new Intent(context, NewAppWidget.class);
        intent.setAction("updateWidgetNow");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.newlayout, pendingIntent);

        GetCoinWebApiAsyncTask getCoin = new GetCoinWebApiAsyncTask(views, widgetText, boughtPrice, appWidgetId, appWidgetManager);
        getCoin.execute();

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            NewAppWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if ("updateWidgetNow".equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, NewAppWidget.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);

            for (int appWidgetId : appWidgetIds) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(NewAppWidgetConfigureActivity.PREFS_NAME, 0);
                String coin = sharedPreferences.getString(NewAppWidgetConfigureActivity.PREF_COINNAME_KEY + appWidgetId, null);
                double boughtPrice = Double.parseDouble(sharedPreferences.getString(NewAppWidgetConfigureActivity.PREF_PREFIX_KEY + appWidgetId, "0"));
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

                Toast.makeText(context, coin + " " + appWidgetId, Toast.LENGTH_SHORT).show();
                GetCoinWebApiAsyncTask getCoin = new GetCoinWebApiAsyncTask(views, coin, boughtPrice, appWidgetId, appWidgetManager);
                getCoin.execute();
            }
        }
    }
}

