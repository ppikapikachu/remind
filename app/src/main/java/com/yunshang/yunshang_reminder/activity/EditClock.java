package com.yunshang.yunshang_reminder.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.yunshang.yunshang_reminder.MyApplication;
import com.yunshang.yunshang_reminder.R;
import com.yunshang.yunshang_reminder.entity.EventMsg;
import com.yunshang.yunshang_reminder.entity.EventRemind;
import com.yunshang.yunshang_reminder.entity.SharedViewModel;
import com.yunshang.yunshang_reminder.service.EventService;
import com.yunshang.yunshang_reminder.service.impl.EventServiceImpl;
import com.yunshang.yunshang_reminder.utils.ConvertUtil;

import org.greenrobot.eventbus.EventBus;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class EditClock extends BaseActivity implements View.OnClickListener {

    private TimePicker timepicker;
    private int cycle=-1;
    private int soundOrBoth=1;//1是默认响铃，2是加上震动
    private int repeateId = 0;
    private RelativeLayout repeat_rl, ring_rl;
    private TextView tv_repeat_value, tv_ring_value, tv_title, tv_msg;
    private Switch switch_ring = null;
    private boolean isVibrate = false;
    private ImageView edit_btn, del_btn;
    private String time;
    private ArrayList<Integer> customizeId = null;//周几的代号，1就是周一
    private Long startTime;
    private SharedViewModel model = null;
    private EventService eventService = new EventServiceImpl();
    private EventRemind remindOld = null;//原本的remind
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_clock);
        viewModel();
        init();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init(){
        edit_btn = findViewById(R.id.edit_btn);
        del_btn = findViewById(R.id.del_btn);
        repeat_rl = findViewById(R.id.repeat_rl);
        tv_repeat_value = findViewById(R.id.tv_repeat_value);
        String val = "";
        if (remindOld.getCustomizeId() != null) {//自定义
            for (Integer d : remindOld.getCustomizeId()) {
                val  = val+ ConvertUtil.formateWeekDay(d);
            }
        }else {
            if (remindOld.getRepeateId() == 0)
                val = "只响一次";
            else
                val = "每天";
        }
        tv_repeat_value.setText(val);//初始化响铃类型
//        tv_ring_value = findViewById(R.id.tv_ring_value);
        switch_ring = findViewById(R.id.switch_ring);
        timepicker = findViewById(R.id.timepicker);
        tv_title = findViewById(R.id.tv_title);
        tv_msg = findViewById(R.id.tv_msg);
        tv_title.setText(remindOld.getTitle()==null?"":remindOld.getTitle());
        tv_msg.setText(remindOld.getMsg()==null?"":remindOld.getMsg());
        edit_btn.setOnClickListener(this);
        del_btn.setOnClickListener(this);
        repeat_rl.setOnClickListener(this);
        timepicker.setIs24HourView(true);//24小时制
//        时间戳转换时分，显示初始化
        Instant instant = Instant.ofEpochMilli(Long.valueOf(remindOld.getStartTime()));
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        timepicker.setHour(zonedDateTime.getHour());  //设置该闹钟小时
        timepicker.setMinute(zonedDateTime.getMinute()); //设置该闹钟分（0-59）
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

        switch_ring.setChecked(soundOrBoth==2?true:false);
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

        remindOld = model.getEventRemind();
        repeateId = remindOld.getRepeateId();
        startTime = Long.valueOf(remindOld.getStartTime());
        customizeId = remindOld.getCustomizeId();
        soundOrBoth = remindOld.getSoundOrboth();

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.repeat_rl:

                Intent intent = new Intent(EditClock.this, RepeateActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); // 仅打开新Activity，不影响原Activity
                startActivity(intent);
                break;
            case R.id.edit_btn://提交修改
                String title = "提醒",msg="闹钟响了";
                if (tv_title.getText() != null && tv_title.getText().toString().trim().length() >0){
                    title = tv_title.getText().toString();
                }
                if (tv_msg.getText() != null && tv_msg.getText().toString().trim().length() > 0){
                    msg = tv_msg.getText().toString();
                }
                remindOld.setStartTime(String.valueOf(startTime));
                remindOld.setTitle(title);
                remindOld.setMsg(msg);
                remindOld.setRepeateId(repeateId);
                remindOld.setCustomizeId(customizeId);
                remindOld.setSoundOrboth(soundOrBoth);
                EventBus.getDefault().post(new EventMsg(5,remindOld));
                finish();
                break;
            case R.id.del_btn:
                new AlertDialog.Builder(this)
                        .setTitle("确认")
                        .setMessage("确定删除？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //                删除一条
                                int re = eventService.delClockById(model.getEventRemind().getId());
                                if (re == 1){
                                    Toast.makeText(EditClock.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    EventBus.getDefault().post(new EventMsg(2,remindOld));//总线通知删除列表
                                    // 和workmanager的该条数据
                                    finish();
                                }
                                else
                                    Toast.makeText(EditClock.this, "删除失败", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("否", null)
                        .show();
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