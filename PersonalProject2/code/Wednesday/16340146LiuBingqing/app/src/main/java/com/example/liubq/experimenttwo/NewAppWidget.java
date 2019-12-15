package com.example.liubq.experimenttwo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {
    private static final String WIDGETSTATICACTION = "com.example.liubq.experimenttwo.MyWidgetStaticFilter";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
//        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        RemoteViews updateView = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);//实例化RemoteView,其对应相应的Widget布局
        Intent i = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        updateView.setOnClickPendingIntent(R.id.widget_image, pi); //设置点击事件
        ComponentName me = new ComponentName(context, NewAppWidget.class);
        appWidgetManager.updateAppWidget(me, updateView);
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
    public void onReceive(Context context, Intent intent ){
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        Bundle bundle = intent.getExtras();
        if(intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE") && bundle.getSerializable("name") != null){
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
            CharSequence widgetText = "今日推荐 "+ bundle.getString("name");
            views.setTextViewText(R.id.appwidget_text, widgetText);

            Intent intent2 = new Intent(context, DetailActivity.class);
            Bundle bundle2 = new Bundle();
            bundle2.putString("name", bundle.getString("name"));
            bundle2.putString("kind", bundle.getString("kind"));
            bundle2.putString("contain", bundle.getString("contain"));
            bundle2.putString("bgcolor", bundle.getString("bgcolor"));
            bundle2.putString("circle", bundle.getString("circle"));
            intent2.putExtras(bundle2);

            PendingIntent pendingIntent=PendingIntent.getActivity(context,0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.appwidget_text,pendingIntent);
            ComponentName me=new ComponentName(context,NewAppWidget.class);
            appWidgetManager.updateAppWidget(me, views);
        }
    }
}

