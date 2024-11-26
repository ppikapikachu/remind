package com.yunshang.yunshang_reminder.clock;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yunshang.yunshang_reminder.MyApplication;
import com.yunshang.yunshang_reminder.entity.EventMsg;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ClockWorkManager extends Worker {
    public ClockWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String customize = getInputData().getString("customizeId");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Integer>>(){}.getType();
        ArrayList customizeArr = gson.fromJson(customize,type);
        String title = getInputData().getString("title");
        String msg = getInputData().getString("msg");
        Integer soundOrVibrator = getInputData().getInt("soundOrVibrator",0);
        if (customizeArr != null){
            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            Integer[] days = {7, 1, 2, 3, 4, 5, 6};
            // 在Calendar类中，星期是从星期日开始的
            System.out.println("今天是" + days[dayOfWeek - 1]);

            if (customizeArr.contains(days[dayOfWeek - 1])){//如果今天在选择的周几内
                new MyNotification().a(MyApplication.getContext(),soundOrVibrator, title,msg);
                //        总线，启动dia
                Map map = new HashMap();
                map.put("title",title);
                map.put("msg",msg);
                map.put("soundOrVibrator",soundOrVibrator);
                EventBus.getDefault().post(new EventMsg(0,map));
            }
        }else {
            new MyNotification().a(MyApplication.getContext(),soundOrVibrator, title,msg);
            //        总线，启动dia
            Map map = new HashMap();
            map.put("title",title);
            map.put("msg",msg);
            map.put("soundOrVibrator",soundOrVibrator);
            EventBus.getDefault().post(new EventMsg(0,map));
        }
        return null;
    }
}
