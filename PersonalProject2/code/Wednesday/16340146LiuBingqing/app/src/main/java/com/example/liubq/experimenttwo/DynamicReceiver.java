package com.example.liubq.experimenttwo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

public class DynamicReceiver extends BroadcastReceiver {
    private static final String DYNAMICACTION = "com.example.liubq.experimenttwo.MyDynamicFilter";
    private static final String CHANNEL_ID = "2";
    private static final String CHANNEL_NAME = "channel2";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DYNAMICACTION)) {    //动作检测
            Bundle bundle = intent.getExtras();
            //TODO:添加Notification部分
            String name = bundle.getString("name");

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
            CharSequence widgetText = "已收藏 "+ bundle.getString("name");
            views.setTextViewText(R.id.appwidget_text, widgetText);
            ComponentName me = new ComponentName(context, NewAppWidget.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(me, views);

            Intent intent1 = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("已收藏")
                    .setContentText(name)
                    .setTicker("您有一条新消息")
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setSmallIcon(R.drawable.empty_star)   //设置通知小ICON（通知栏），可以用以前的素材，例如空星
                    .setContentIntent(pendingIntent)   //传递内容
                    .setAutoCancel(true);   //设置这个标志当用户单击面板就可以让通知将自动取消

            Notification notification = mBuilder.build();
            manager.notify(1,notification);
        }
    }
}