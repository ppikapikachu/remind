package com.yunshang.yunshang_reminder.clock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.yunshang.yunshang_reminder.MyApplication;
import com.yunshang.yunshang_reminder.R;
import com.yunshang.yunshang_reminder.activity.MainActivity;
import com.yunshang.yunshang_reminder.entity.EventMsg;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

public class MyNotification {
    String CHANNEL_ID = "my_high_importance_channel"; // 固定的渠道ID
    int NOTIFICATION_ID = 1; // 固定的通知ID，或者使用一个递增的整数
    public void a(Context context,int soundOrVibrator, String title, String msg){
        // 检查通知权限是否已经授予
        boolean notificationPermissionGranted = NotificationManagerCompat.from(context).areNotificationsEnabled();

        if (!notificationPermissionGranted) {
            // 请求通知权限
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            context.startActivity(intent);
        }

        NotificationManager mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "mHighChannelName", NotificationManager.IMPORTANCE_HIGH);
            channel.setShowBadge(true);
            mManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(msg)
//                .setDefaults(Notification.DEFAULT_SOUND)//声音，默认
//                .setDefaults(Notification.DEFAULT_VIBRATE)//震动，默认
//                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_avatar))
                .setAutoCancel(false)
                .setOngoing(true)
//                .setNumber(999) // 自定义桌面通知数量
                .setCategory(NotificationCompat.CATEGORY_MESSAGE) // 通知类别，"勿扰模式"时系统会决定要不要显示你的通知
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE); // 屏幕可见性，锁屏时，显示icon和标题，内容隐藏
        mBuilder.setVibrate(new long[]{0,100, 10, 100, 10});

//        启动mainactivity
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getContext().startActivity(mainIntent);



        Intent clockIntent = new Intent(context, MainActivity.class);
        clockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        clockIntent.putExtra("title",title);
//        clockIntent.putExtra("msg", msg);
//        clockIntent.putExtra("soundOrVibrator", soundOrVibrator);
        PendingIntent sender;
        sender = PendingIntent.getActivity(context, 1, clockIntent, PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE);
        mBuilder.addAction(R.mipmap.ic_launcher,"去看看",sender);
        mBuilder.setContentIntent(sender); // 跳转配置
        EventBus.getDefault().post(new EventMsg(10,null));//关闭除main外的所有activity
        ClockDiaAndSoundWithVibrator.showDialogInBroadcastReceiver(null, soundOrVibrator);
        mManager.notify(NOTIFICATION_ID, mBuilder.build());

//        开启dialog

        Map map = new HashMap();
        map.put("title",title);
        map.put("msg",msg);
        map.put("soundOrVibrator",soundOrVibrator);
//        发送粘性事件
        EventBus.getDefault().postSticky(new EventMsg(0,map));
    }


}
