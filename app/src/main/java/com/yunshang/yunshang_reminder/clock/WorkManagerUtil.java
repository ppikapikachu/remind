package com.yunshang.yunshang_reminder.clock;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.gson.Gson;
import com.yunshang.yunshang_reminder.MyApplication;
import com.yunshang.yunshang_reminder.entity.EventRemind;
import com.yunshang.yunshang_reminder.service.EventService;
import com.yunshang.yunshang_reminder.service.impl.EventServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class WorkManagerUtil {
//    String flag = getInputData().getString("flag");
//    String createTime = getInputData().getString("createTime");
//    String startTime = getInputData().getString("startTime");
//    String customizeId = getInputData().getString("customizeId");
//    String title = getInputData().getString("title");
//    String msg = getInputData().getString("msg");
//    String soundOrVibrator = getInputData().getString("soundOrVibrator");

    private static String ONCE = "ONCE";//一次
    private static String EVERYDAY = "EVERYDAY";//每天
    private static String CUSTOMIZE = "CUSTOMIZE";//自定义
    private static EventService eventService = new EventServiceImpl();

    /**
     * @param flag            周期性时间间隔的标志,flag = 0 表示一次性的闹钟, flag = 1 表示每天提醒的闹钟(1天的时间间隔),flag = 2
     *                        表示按周每周提醒的闹钟（一周的周期性时间间隔）
     * @param createTime      创建时间
     * @param startTime       开始提醒时间
     * @param customizeId     日期集合，1就是周1，null就代表flag不是自定义的
     * @param title           闹钟标题
     * @param msg             闹钟信息
     * @param soundOrVibrator 2表示声音和震动都执行，1表示只有铃声提醒，0表示只有震动提醒
     */
    public static void setWork(int flag, Long createTime, Long startTime, ArrayList<Integer> customizeId, String
            title, String msg, int soundOrVibrator) throws ExecutionException, InterruptedException {
        String f ;
        WorkRequest workRequest ;
        Data input;
        long time =startTime - createTime;
        if (time < 0){
            time = 1000*60*60*24 - Math.abs(time);
        }
        time = 5000;//测试，五秒后响
        if (flag == 0){//一次
            f = ONCE;
            input = new Data.Builder().putString("title",title).putString("msg",msg).putInt("soundOrVibrator",soundOrVibrator).build();//自定义
            workRequest = new OneTimeWorkRequest.Builder(ClockWorkManager.class)
                    .setInputData(input)
//                    .setInitialDelay(time, TimeUnit.MINUTES)
                    .setInitialDelay(time,TimeUnit.MILLISECONDS)
                    .addTag(f)//标签
                    .build();
        }
        else if (flag == 1){//每天
            f = EVERYDAY;
            input = new Data.Builder().putString("title",title).putString("msg",msg).putInt("soundOrVibrator",soundOrVibrator).build();//自定义
            workRequest = new PeriodicWorkRequest.Builder(ClockWorkManager.class,24,TimeUnit.HOURS)
                    .setInputData(input)
                    .setInitialDelay(time,TimeUnit.MILLISECONDS)
                    .addTag(f)
                    .build();
        }
        else{//自定义
            f = CUSTOMIZE;
            Gson gson = new Gson();
            String cus = gson.toJson(customizeId);
            Map<String, Object> map = new HashMap();
            map.put("customize",customizeId);
            input = new Data.Builder().putString("customizeId",cus).putString("title",title).putString("msg",msg).putInt("soundOrVibrator",soundOrVibrator).build();//自定义
            workRequest = new PeriodicWorkRequest.Builder(ClockWorkManager.class,24,TimeUnit.HOURS)
                    .setInputData(input)
                    .setInitialDelay(time,TimeUnit.MILLISECONDS)
                    .addTag(f)
                    .build();
        }

        WorkManager.getInstance(MyApplication.getContext()).enqueue(workRequest);
        @SuppressLint("RestrictedApi") String id = workRequest.getStringId();//系统分配的唯一id

        Long add = eventService.add(new EventRemind(id, flag,String.valueOf(createTime),
                String.valueOf(startTime),1, customizeId, title, msg));//第一次创建添加闹钟status默认为开启
        Log.i("添加数据库：：",add +"    ");
    }
}
