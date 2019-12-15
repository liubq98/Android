package com.example.liubq.experimenttwo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class StaticReceiver extends BroadcastReceiver {
    private static final String STATICACTION = "com.example.liubq.experimenttwo.MyStaticFilter";
    private static final String CHANNEL_ID = "1";
    private static final String CHANNEL_NAME = "channel";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(STATICACTION)){
            Bundle bundle = intent.getExtras();

            String name = bundle.getString("name");
            Intent intent1 = new Intent(context, DetailActivity.class);
            Bundle bundle2 = new Bundle();
            bundle2.putString("name", name);
            bundle2.putString("kind", bundle.getString("kind"));
            bundle2.putString("contain", bundle.getString("contain"));
            bundle2.putString("bgcolor", bundle.getString("bgcolor"));
            bundle2.putString("circle", bundle.getString("circle"));
            intent1.putExtras(bundle2);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("今日推荐")
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
