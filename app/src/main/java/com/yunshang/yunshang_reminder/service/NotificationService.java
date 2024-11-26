package com.yunshang.yunshang_reminder.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.yunshang.yunshang_reminder.MyApplication;
import com.yunshang.yunshang_reminder.R;

import java.util.Calendar;

public class NotificationService extends Service {
    private static final String TAG = "ForeService";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    public NotificationService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("qidongqidong","启动service");
        // 创建前台服务通知
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("前台服务")
                .setContentText("服务正在运行")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        startForeground(NOTIFICATION_ID, notification);

        // 获取AlarmManager实例
        alarmManager = (AlarmManager) MyApplication.getContext().getSystemService(Context.ALARM_SERVICE);

        // 设置一个一次性闹钟（这里为了演示，设置为10秒后触发）
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 3);

        Intent intent1 = new Intent("com.yunshang.yunshang_reminder.clock");
//        广播接收器的包名和全限定类名
        intent1.setClassName("com.yunshang.yunshang_reminder","com.yunshang.yunshang_reminder.clock.LoongggAlarmReceiver");
        intent1.putExtra("intervalMillis", 1);
        intent1.putExtra("msg", "tips");
        intent1.putExtra("id", 1);
        intent1.putExtra("soundOrVibrator", 1);//提醒方式，震动之类
        pendingIntent = PendingIntent.getBroadcast(this, 1, intent1, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        return super.onStartCommand(intent, flags, startId);
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知
//    }
}
