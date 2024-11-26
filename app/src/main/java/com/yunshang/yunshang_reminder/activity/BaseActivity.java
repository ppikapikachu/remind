package com.yunshang.yunshang_reminder.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.yunshang.yunshang_reminder.R;
import com.yunshang.yunshang_reminder.entity.EventMsg;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventMsg e){
        switch (e.getId()){
            case 10://dia相关
                Log.i("接收总线数据：",e.getId()+"");
                finish();
                break;
        }
    }
}
