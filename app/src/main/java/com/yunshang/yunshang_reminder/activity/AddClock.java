package com.yunshang.yunshang_reminder.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bigkoo.pickerview.TimePickerView;
import com.yunshang.yunshang_reminder.MyApplication;
import com.yunshang.yunshang_reminder.R;
import com.yunshang.yunshang_reminder.entity.EventMsg;
import com.yunshang.yunshang_reminder.entity.EventRemind;
import com.yunshang.yunshang_reminder.entity.SharedViewModel;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class AddClock extends BaseActivity implements View.OnClickListener {

    private TimePicker timepicker;
    private int cycle=-1;
    private int soundOrBoth=1;//1是默认响铃，2是加上震动
    private int repeateId = 0;
    private RelativeLayout repeat_rl, ring_rl;
    private TextView tv_repeat_value, tv_ring_value, tv_title, tv_msg;
    private Switch switch_ring = null;
    private boolean isVibrate = false;
    private ImageView set_btn;
    private String time;
    private ArrayList<Integer> customizeId = null;//周几的代号，1就是周一
    private Long startTime;
    private SharedViewModel model = null;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addclock);
        init();
        viewModel();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init(){
        set_btn = findViewById(R.id.set_btn);
        repeat_rl = findViewById(R.id.repeat_rl);
        tv_repeat_value = findViewById(R.id.tv_repeat_value);
        tv_repeat_value.setText("只响一次");//初始化响铃类型
//        tv_ring_value = findViewById(R.id.tv_ring_value);
        switch_ring = findViewById(R.id.switch_ring);
        timepicker = findViewById(R.id.timepicker);
        tv_title = findViewById(R.id.tv_title);
        tv_msg = findViewById(R.id.tv_msg);
        set_btn.setOnClickListener(this);
        repeat_rl.setOnClickListener(this);
        timepicker.setIs24HourView(true);//24小时制
        timepicker.setHour(LocalTime.now().getHour());  //设置当前小时
        timepicker.setMinute(LocalTime.now().getMinute()); //设置当前分（0-59）
        startTime = getTime(timepicker.getHour(),timepicker.getMinute());//赋默认值
        timepicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                Log.i("选择时间：",i+"::"+i1);
                startTime = getTime(i,i1);//获取选择时间的时间戳
            }
        });
        //        不允许手动输入
        timepicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

        switch_ring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isVibrate = b;
                soundOrBoth = (b == true?2:1);
            }
        });
    }
    private void viewModel() {
        model = new ViewModelProvider(MyApplication.getInstance()).get(SharedViewModel.class);
        model.getRepeateId().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                tv_repeat_value.setText(model.getValue());
//                赋值闹钟时间
                repeateId = integer;
                customizeId = model.getCustomizeId();
                Log.i("MainActivity接收到customizeId：",customizeId+"");
            }
        });

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.repeat_rl:

                Intent intent = new Intent(AddClock.this, RepeateActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); // 仅打开新Activity，不影响原Activity
                startActivity(intent);
                break;
            case R.id.set_btn:
                String title = "提醒",msg="闹钟响了";
                if (tv_title.getText() != null && tv_title.getText().toString().trim().length() >0){
                    title = tv_title.getText().toString();
                }
                if (tv_msg.getText() != null && tv_msg.getText().toString().trim().length() > 0){
                    msg = tv_msg.getText().toString();
                }
                EventBus.getDefault().post(new EventMsg(1,new EventRemind(repeateId,String.valueOf(startTime),
                        customizeId,title,msg,soundOrBoth)));
                finish();
                break;
        }
    }

//    将时钟分钟转时间戳
    public Long getTime(int hour, int minute) {

//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int minute = calendar.get(Calendar.MINUTE);

        // 假设我们选择的毫秒部分为0
        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.set(Calendar.HOUR_OF_DAY, hour);
        selectedCalendar.set(Calendar.MINUTE, minute);
        selectedCalendar.set(Calendar.SECOND, 0);
        selectedCalendar.set(Calendar.MILLISECOND, 0);

        long timestamp = selectedCalendar.getTimeInMillis();
        String formattedDate = String.format(Locale.getDefault(), "时间戳: %d", timestamp);
        Log.i("转化时间戳::",formattedDate+"");

//        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
//        return format.format(date);
        return timestamp;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        model.getRepeateId().removeObservers(this);
        model.reset();
    }
}