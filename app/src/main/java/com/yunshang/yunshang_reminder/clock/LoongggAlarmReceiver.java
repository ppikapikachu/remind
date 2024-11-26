//package com.yunshang.yunshang_reminder.clock;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.media.MediaPlayer;
//import android.os.Build;
//import android.os.Vibrator;
//import android.provider.Settings;
//import android.util.Log;
//
//import androidx.core.app.NotificationCompat;
//import androidx.core.app.NotificationManagerCompat;
//
//import com.yunshang.yunshang_reminder.MyApplication;
//import com.yunshang.yunshang_reminder.R;
//
//
//public class LoongggAlarmReceiver extends BroadcastReceiver {
//    private MediaPlayer mediaPlayer;
//    private Vibrator vibrator;
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
////        context = MyApplication.getContext();
//        Log.i("receive接收到消息","receive receive receive receive");
//        // TODO Auto-generated method stub
//        String msg = intent.getStringExtra("msg");
//        long intervalMillis = intent.getLongExtra("intervalMillis", 0);
//        if (intervalMillis != 0) {
//            AlarmManagerUtil.setAlarmTime(context, System.currentTimeMillis() + intervalMillis,
//                    intent);
//        }
//        int flag = intent.getIntExtra("soundOrVibrator", 0);
//        Intent clockIntent = new Intent(context, ClockAlarmActivity.class);
//        clockIntent.putExtra("msg", msg);
//        clockIntent.putExtra("flag", flag);
//        Log.i("flag即提醒方式为：",flag+";';'';';';;';'';"+msg);
//        clockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
////        context.startActivity(clockIntent);
//
//        new ClockAlarmActivity().showDialogInBroadcastReceiver(msg,flag);
//        new MyNotification().a(context,flag, "123","414141");
//    }
//
//    String CHANNEL_ID = "my_high_importance_channel"; // 固定的渠道ID
//    int NOTIFICATION_ID = 1; // 固定的通知ID，或者使用一个递增的整数
//    public void a(Context context){
//        // 检查通知权限是否已经授予
//        boolean notificationPermissionGranted = NotificationManagerCompat.from(context).areNotificationsEnabled();
//
//        if (!notificationPermissionGranted) {
//            // 请求通知权限
//            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
//                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
//            context.startActivity(intent);
//        }
//
//        NotificationManager mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "mHighChannelName", NotificationManager.IMPORTANCE_HIGH);
//            channel.setShowBadge(true);
//            mManager.createNotificationChannel(channel);
//        }
//
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,CHANNEL_ID)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("重要通知")
//                .setContentText("重要通知内容")
//                .setDefaults(Notification.DEFAULT_SOUND)//声音，默认
//                .setDefaults(Notification.DEFAULT_VIBRATE)//震动，默认
////                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_avatar))
//                .setAutoCancel(true)
////                .setNumber(999) // 自定义桌面通知数量
////                .addAction(R.mipmap.ic_avatar, "去看看", pendingIntent)// 通知上的操作
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE) // 通知类别，"勿扰模式"时系统会决定要不要显示你的通知
//                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE); // 屏幕可见性，锁屏时，显示icon和标题，内容隐藏
//        mManager.notify(NOTIFICATION_ID, mBuilder.build());
//        Log.i("1231231231","123123");
//    }
//}
