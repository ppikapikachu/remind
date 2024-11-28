package com.yunshang.yunshang_reminder.clock;

import android.app.Service;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import com.yunshang.yunshang_reminder.MyApplication;
import com.yunshang.yunshang_reminder.R;
import com.yunshang.yunshang_reminder.view.SimpleDialog;

public class ClockDiaAndSoundWithVibrator {

    private static MediaPlayer mediaPlayer;
    private static Vibrator vibrator;//振动器

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
    public static void dia(Context context, String title, String message, final int soundOrVibrator){

        final SimpleDialog dialog = new SimpleDialog(context, R.style.Theme_dialog);
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
