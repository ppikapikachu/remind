package com.yunshang.yunshang_reminder.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.TimePicker;

import com.yunshang.yunshang_reminder.R;

import java.time.LocalTime;

public class AddClock extends BaseActivity {

    private TimePicker timepicker;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addclock);
        init();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init(){
        timepicker = findViewById(R.id.timepicker);
        timepicker.setIs24HourView(true);//24小时制
        timepicker.setHour(LocalTime.now().getHour());  //设置当前小时
        timepicker.setMinute(LocalTime.now().getMinute()); //设置当前分（0-59）

    }
}