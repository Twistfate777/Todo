package com.example.todo;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

public class AlarmManagerUtils {

    private final String TAG = "AlarmManagerUtils";
    private static final long TIME_INTERVAL = 10 * 10;//闹钟执行任务的时间间隔
    private Context context;
    public static AlarmManager alarmManager;
    public PendingIntent pendingIntent;

    private Calendar calendar;

    private AlarmManagerUtils(Context aContext) {
        this.context = aContext;
    }

    private static AlarmManagerUtils instance = null;

    //单例模式
    public static AlarmManagerUtils getInstance(Context aContext) {
        if (instance == null) {
           // synchronized (AlarmManagerUtils.class) 锁住，防止多个用户同时访问
            {
                if (instance == null) {
                    instance = new AlarmManagerUtils(aContext);
                    alarmManager = (AlarmManager) aContext.getSystemService(Context.ALARM_SERVICE);
                }
            }
        }
        return instance;
    }

    public PendingIntent createPendingIntent(int id,int fid,int deadline)
    {
        Intent intent = new Intent(context, MyReceiver.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);//虽然app关闭了，但是闹钟还是运行的
        intent.putExtra("id",id);
        intent.putExtra("fid",fid);
        //发送特定广播
        PendingIntent _pendingIntent = PendingIntent.getBroadcast(context, deadline, intent,0);
        return _pendingIntent;
    }

    public  void StartPIWork(PendingIntent _pendingIntent,int y,int m,int d,int h, int minute)
    {
        calendar = Calendar.getInstance();

        calendar.set(y,m,d,h,minute);


        //版本适配 System.currentTimeMillis()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {// 6.0及以上
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), _pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// 4.4及以上
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    pendingIntent);
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), TIME_INTERVAL, pendingIntent);
        }
    }
}