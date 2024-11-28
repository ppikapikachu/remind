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
import androidx.work.WorkManager;

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
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = MainActivity.class.getSimpleName();

    private TimePickerView pvTime;

    private ClockAdapter clockAdapter = null;
    private EventService eventService = new EventServiceImpl();
    private RecyclerView clock_recyclerview = null;
    private ImageButton addClock;
    private List<EventRemind> allClock = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initAdapterView();//初始化适配器数据
        //获取默认的EventBus对象(单例)，并把当前对象注册为Subscriber。
        //注意：当销毁当前实例的时候必须注销这个Subscriber。一般与Activity的生命周期绑定
        EventBus.getDefault().register(this);
        EventMsg stickyEvent = EventBus.getDefault().getStickyEvent(EventMsg.class);
        Log.i("获取到粘性事件：",stickyEvent+"");
        if (stickyEvent != null){
            String title = (String) ((Map) stickyEvent.getData()).get("title");
            String msg = (String) ((Map) stickyEvent.getData()).get("msg");
            int soundOrVibrator = (int) ((Map) stickyEvent.getData()).get("soundOrVibrator");
            ClockDiaAndSoundWithVibrator.dia(this, title, msg, soundOrVibrator);//开启dialog
            EventBus.getDefault().removeStickyEvent(stickyEvent);//移除粘性事件

        }

    }

    private void init() {

        clock_recyclerview = findViewById(R.id.clock_recyclerview);
        addClock = findViewById(R.id.addClock);
        clock_recyclerview.setLayoutManager(new LinearLayoutManager(this));

        addClock.setOnClickListener(this);

        pvTime = new TimePickerView(this, TimePickerView.Type.HOURS_MINS);
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);

        //时间选择后回调
//        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
//            @Override
//            public void onTimeSelect(Date date) {
//                time = getTime(date);
//                date_tv.setText(time);
//            }
//        });
//        date_tv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                pvTime.show();
//            }
//        });

    }

    private void initAdapterView() {
        allClock = eventService.getAllClock();
        Log.i("数据库的allClock:", allClock + "");
        clockAdapter = new ClockAdapter(this, allClock);
        clock_recyclerview.setAdapter(clockAdapter);//设置适配器

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addClock:
                Intent intent1 = new Intent(this, AddClock.class);
                startActivity(intent1);
                break;

            default:
                break;
        }
    }


    private EventRemind setClock(int repeateId, String startTime, ArrayList<Integer> customizeId, String title, String msg, int soundOrboth) throws ExecutionException, InterruptedException {

        EventRemind remind = null;
        if (repeateId == 1) {//是每天的闹钟
            remind = WorkManagerUtil.setWork(1,1, System.currentTimeMillis(), Long.valueOf(startTime), null,
                    title, msg, soundOrboth);
        }
        else if (repeateId == 0) {//是只响一次的闹钟

            remind = WorkManagerUtil.setWork(1,0, System.currentTimeMillis(), Long.valueOf(startTime), null,
                    title, msg, soundOrboth);

        } else {//多选，周几的闹钟
//                for (int i = 0; i < customizeId.size(); i++) {
//                    AlarmManagerUtil.setAlarm(this, 2, Integer.parseInt(times[0]), Integer
//                            .parseInt(times[1]), i, i, "闹钟响了1111", ring);
//                }
            remind = WorkManagerUtil.setWork(1,2, System.currentTimeMillis(), Long.valueOf(startTime), customizeId,
                    title, msg, soundOrboth);
        }
        Toast.makeText(this, "闹钟设置成功", Toast.LENGTH_SHORT).show();
        return remind;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventMsg e) throws ExecutionException, InterruptedException {
        switch (e.getId()) {
            case 0://dia相关
                Log.i("弹窗弹窗弹窗","");
                if (e.getData() == null || !(e.getData() instanceof Map))
                    break;
                String title = (String) ((Map) e.getData()).get("title");
                String msg = (String) ((Map) e.getData()).get("msg");
                int soundOrVibrator = (int) ((Map) e.getData()).get("soundOrVibrator");
                ClockDiaAndSoundWithVibrator.dia(this, title, msg, soundOrVibrator);//开启dialog
                break;
            case 1://addclock提交新增的闹钟
                if (e.getData() == null || !(e.getData() instanceof EventRemind))
                    break;
                EventRemind remind = (EventRemind)e.getData();
                EventRemind remindR = setClock(remind.getRepeateId(), remind.getStartTime(), remind.getCustomizeId(), remind.getTitle(),
                        remind.getMsg(), remind.getSoundOrboth());
                if (remindR == null){
                    Toast.makeText(this, "设置闹钟失败", Toast.LENGTH_SHORT).show();
                    break;
                }
                allClock.add(remindR);
                clockAdapter.notifyDataSetChanged();//添加一个，刷新列表
                break;
            case 2://在编辑页中删除和系统任务重中取消
                if (e.getData() == null || !(e.getData() instanceof EventRemind))
                    break;
                EventRemind remindD = (EventRemind) e.getData();
                String id = remindD.getId();//获取被删除的item的id
                WorkManager.getInstance(MyApplication.getContext()).cancelWorkById(UUID.fromString(id));//取消任务
                allClock.remove(remindD);//删掉该条目
                clockAdapter.notifyDataSetChanged();//刷新
                break;
            case 3://一次性任务执行完毕，改为未启用状态，刷新数据源和适配器.
                if (e.getData() == null || !(e.getData() instanceof EventRemind))
                    break;
                EventRemind remindU = (EventRemind) e.getData();
                int i = findFromList(allClock,remindU);
                if (i == -1){
                    Log.i("适配器中不存在","fail");
                    break;
                }
                Log.i("一次性任务刷新数据源：",i+"");
                // TODO: 2024/11/26 这里找不到下标
                remindU.setStatus(0);
                allClock.set(i,remindU);
                clockAdapter.notifyDataSetChanged();
                break;
            case 4://刷新数据，在适配器中，点击开启关闭后可以调用
                Log.i("刷新数据；","sxsj");
                clockAdapter.notifyDataSetChanged();
                Log.i("更新后的数据源：",allClock+"");
                break;
            case 5://修改闹钟
                if (e.getData() == null || !(e.getData() instanceof EventRemind))
                    break;
                EventRemind remindUA = (EventRemind) e.getData();
                int update = eventService.update(null, remindUA);
                if (update != 0){
                    Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                    int ii = findFromList(allClock,remindUA);
                    if (ii == -1){
                        Log.i("适配器中不存在","fail");
                        break;
                    }
                    allClock.set(ii,remindUA);
                    clockAdapter.notifyDataSetChanged();
                }
                break;
        }
    }
    private int findFromList(List<EventRemind> arr, EventRemind e){
        int j = 0;
        for (EventRemind temp:arr){
            if (temp.getId().equals(e.getId()))
                return j;
            j++;
        }
        return -1;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//反注册总线
    }
}