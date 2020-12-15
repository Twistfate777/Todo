package com.example.todo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;


import static android.content.Context.NOTIFICATION_SERVICE;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        final int fid = intent.getIntExtra("fid",-1);
        final int id = intent.getIntExtra("id",-1);

        GlobleData gl = GlobleData.getInstance(context);
        final TodoTaskDao dao = GlobleData.getDB().mTodoTaskDao();
        new Thread(new Runnable() {
            @Override
            public void run() {
                TodoTask task = dao.getTask(fid,id);
                String taskName = task.name;
                String comment = task.comment;

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                String channelId = Double.toString( (Math.random()));
                NotificationChannel channel = new NotificationChannel(channelId,"Todo", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
                Notification test = new NotificationCompat.Builder(context, channelId)
                        .setContentTitle(taskName)
                        .setContentText(comment)
                        .setSmallIcon(R.mipmap.logo_icon_foreground)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.logo_icon_foreground))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();

                notificationManager.notify((int)(Math.random() * 10000),test);

                task.deadLineID = -1;
                dao.update(task);
            }
        }).start();
    }
}
