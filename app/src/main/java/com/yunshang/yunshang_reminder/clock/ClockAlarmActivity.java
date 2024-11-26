package com.yunshang.yunshang_reminder.clock;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.app.Activity;
import android.app.Service;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import com.yunshang.yunshang_reminder.MyApplication;
import com.yunshang.yunshang_reminder.R;


public class ClockAlarmActivity extends Activity {
    private static MediaPlayer mediaPlayer;
    private static Vibrator vibrator;//振动器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_alarm);
        finish();
//        String message = this.getIntent().getStringExtra("msg");
//        String title = this.getIntent().getStringExtra("title");
//        int soundOrVibrator = this.getIntent().getIntExtra("soundOrVibrator", 0);
////        showDialogInBroadcastReceiver(message, flag);
//        dia(title, message, soundOrVibrator);
    }

//
    public static void showDialogInBroadcastReceiver(String message, final int soundOrVibrator) {
        Log.i("soundOrVibrator的值：",soundOrVibrator+"[][]][]");
        if (soundOrVibrator == 1 || soundOrVibrator == 2) {
            mediaPlayer = MediaPlayer.create(MyApplication.getContext(), R.raw.in_call_alarm);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
        //数组参数意义：第一个参数为等待指定时间后开始震动，震动时间为第二个参数。后边的参数依次为等待震动和震动的时间
        //第二个参数为重复次数，-1为不重复，0为一直震动
        if (soundOrVibrator == 2) {
            vibrator = (Vibrator) MyApplication.getContext().getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(new long[]{100, 10, 100, 600}, 0);
        }

    }
    private void dia(String title, String message, final int soundOrVibrator){

        final SimpleDialog dialog = new SimpleDialog(this, R.style.Theme_dialog);
        dialog.show();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.bt_confirm == v || dialog.bt_cancel == v) {
                    if (soundOrVibrator == 1 || soundOrVibrator == 2) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                    if (soundOrVibrator == 0 || soundOrVibrator == 2) {
                        vibrator.cancel();
                    }
                    dialog.dismiss();
                    finish();
                }
            }
        });
    }
//    停止闹钟的动静
    public static void stopSoundAndVibrator(int soundOrVibrator){
        if (soundOrVibrator == 1 || soundOrVibrator == 2) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if (soundOrVibrator == 0 || soundOrVibrator == 2) {
            vibrator.cancel();
        }
    }
}
