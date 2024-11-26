package com.yunshang.yunshang_reminder.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.pickerview.TimePickerView;
import com.yunshang.yunshang_reminder.MyApplication;
import com.yunshang.yunshang_reminder.R;
import com.yunshang.yunshang_reminder.clock.ClockDiaAndSoundWithVibrator;
import com.yunshang.yunshang_reminder.clock.WorkManagerUtil;
import com.yunshang.yunshang_reminder.clock.adapter.ClockAdapter;
import com.yunshang.yunshang_reminder.entity.EventMsg;
import com.yunshang.yunshang_reminder.entity.EventRemind;
import com.yunshang.yunshang_reminder.entity.SharedViewModel;
import com.yunshang.yunshang_reminder.service.EventService;
import com.yunshang.yunshang_reminder.service.impl.EventServiceImpl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = MainActivity.class.getSimpleName();
    private TextView date_tv;
    private TimePickerView pvTime;
    private RelativeLayout repeat_rl, ring_rl;
    private TextView tv_repeat_value, tv_ring_value;
    private Switch switch_ring = null;
    private boolean isVibrate = false;
    private Button set_btn;
    private String time;
    private int cycle=-1;
    private int ring=1;//1是默认响铃，2是加上震动
    private int repeateId = 0;
    private ArrayList<Integer> customizeId = null;//周几的代号，1就是周一
    private Long createDate;
    private SharedPreferences sp = MyApplication.getContext().getSharedPreferences("data", MODE_PRIVATE);
    private SharedPreferences.Editor editor = sp.edit();
    private ClockAdapter clockAdapter = null;
    private EventService eventService = new EventServiceImpl();
    private RecyclerView clock_recyclerview = null;
    private ImageButton addClock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        viewModel();
        initAdapterView();//初始化适配器数据
        //获取默认的EventBus对象(单例)，并把当前对象注册为Subscriber。
        //注意：当销毁当前实例的时候必须注销这个Subscriber。一般与Activity的生命周期绑定
        EventBus.getDefault().register(this);
    }

    private void init() {
        set_btn = findViewById(R.id.set_btn);
        date_tv = findViewById(R.id.date_tv);
        repeat_rl = findViewById(R.id.repeat_rl);
        ring_rl = findViewById(R.id.ring_rl);
        tv_repeat_value = findViewById(R.id.tv_repeat_value);
        tv_repeat_value.setText("只响一次");//初始化响铃类型
//        tv_ring_value = findViewById(R.id.tv_ring_value);
        switch_ring = findViewById(R.id.switch_ring);
        clock_recyclerview = findViewById(R.id.clock_recyclerview);
        addClock = findViewById(R.id.addClock);
        clock_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        set_btn.setOnClickListener(this);
        repeat_rl.setOnClickListener(this);
        ring_rl.setOnClickListener(this);
        addClock.setOnClickListener(this);

        pvTime = new TimePickerView(this, TimePickerView.Type.HOURS_MINS);
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);

        //时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                time = getTime(date);
                date_tv.setText(time);
            }
        });
        date_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pvTime.show();
            }
        });

        switch_ring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isVibrate = b;
                ring = (b == true?2:1);
            }
        });
    }

    private void viewModel() {
        SharedViewModel model = new ViewModelProvider(MyApplication.getInstance()).get(SharedViewModel.class);
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
    private void initAdapterView(){
        List<EventRemind> allClock = eventService.getAllClock();
        Log.i("数据库的allClock:",allClock+"");
        clockAdapter = new ClockAdapter(this,allClock);
        clock_recyclerview.setAdapter(clockAdapter);//设置适配器

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addClock:
                Intent intent1 = new Intent(this,AddClock.class);
                startActivity(intent1);
                break;
            case R.id.repeat_rl:

                Intent intent = new Intent(MainActivity.this, RepeateActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); // 仅打开新Activity，不影响原Activity
                startActivity(intent);
//                initDialog();
//                selectRemindCycle();
                break;
            case R.id.ring_rl:
//                selectRingWay();
                break;
            case R.id.set_btn:

                try {
                    setClock();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    public String getTime(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // 假设我们选择的毫秒部分为0
        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.set(Calendar.HOUR_OF_DAY, hour);
        selectedCalendar.set(Calendar.MINUTE, minute);
        selectedCalendar.set(Calendar.SECOND, 0);
        selectedCalendar.set(Calendar.MILLISECOND, 0);

        long timestamp = selectedCalendar.getTimeInMillis();
        String formattedDate = String.format(Locale.getDefault(), "时间戳: %d", timestamp);
        createDate = timestamp;
        Log.i("转化时间戳::",formattedDate+"");

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);

    }

    private void setClock() throws ExecutionException, InterruptedException {
        if (time != null && time.length() > 0) {
            String[] times = time.split(":");
            if (repeateId == 1) {//是每天的闹钟
                WorkManagerUtil.setWork(1,System.currentTimeMillis(),createDate,null,
                        "这是title","这是msg11111",ring);
            }
            if (repeateId == 0) {//是只响一次的闹钟

                WorkManagerUtil.setWork(0,System.currentTimeMillis(),createDate,null,
                        "这是title","这是msg11111",ring);

            } else {//多选，周几的闹钟
//                for (int i = 0; i < customizeId.size(); i++) {
//                    AlarmManagerUtil.setAlarm(this, 2, Integer.parseInt(times[0]), Integer
//                            .parseInt(times[1]), i, i, "闹钟响了1111", ring);
//                }
                WorkManagerUtil.setWork(2,System.currentTimeMillis(),createDate,customizeId,
                        "这是title","这是msg11111",ring);
            }
            Toast.makeText(this, "闹钟设置成功", Toast.LENGTH_LONG).show();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventMsg e){
        switch (e.getId()){
            case 0://dia相关
                if (e.getData()==null || !(e.getData() instanceof Map))
                    break;
                String title = (String) ((Map)e.getData()).get("title");
                String msg = (String) ((Map)e.getData()).get("msg");
                int soundOrVibrator = (int) ((Map)e.getData()).get("soundOrVibrator");
                ClockDiaAndSoundWithVibrator.dia(this,title,msg,soundOrVibrator);//开启dialog
                break;
            case 1:

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//反注册总线
    }
}