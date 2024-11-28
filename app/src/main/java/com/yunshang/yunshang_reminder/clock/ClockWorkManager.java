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
import com.yunshang.yunshang_reminder.entity.EventRemind;
import com.yunshang.yunshang_reminder.service.EventService;
import com.yunshang.yunshang_reminder.service.impl.EventServiceImpl;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ClockWorkManager extends Worker {
    private EventService eventService = new EventServiceImpl();
    public ClockWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    // TODO: 2024/11/26  1、这里getId获取id，然后从数据库找到记录，判断开启状态，没开就不响。
    //  2、获取数据库后还要判断是否只响一次，是则响完关闭闹钟。
    //  3、创建的一次性任务，响一次后就消失了，后续再次启用怎么办.   启动时在数据库中找到数据重新提交一个一样的一次性闹钟.启动一次性事件时在数据库
    //      status，同时查workmanager内这个id事件存不存在，不存在重新提交新的。禁用也改status。在dowork加status判断

    @NonNull
    @Override
    public Result doWork() {
        EventRemind remind = eventService.searchById(getId().toString());
        Log.i("当前正处于的任务id：",getId().toString());
        Log.i("当前执行的remind:",remind+"");
        if (remind.getStatus() == 0){//已经关闭的就不用响了。一次性任状态会变为终止
            return Result.failure();
        }
        if (remind.getRepeateId() == 0) {//一次性任务，将状态改为未启用
            eventService.update(null,new EventRemind(remind.getId(),null,null,null,
                    0,null,null,null,null));
            EventBus.getDefault().post(new EventMsg(3,remind));
        }

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
//                EventBus.getDefault().post(new EventMsg(0,map));
            }
        }else {
            new MyNotification().a(MyApplication.getContext(),soundOrVibrator, title,msg);
            //        总线，启动dia
            Map map = new HashMap();
            map.put("title",title);
            map.put("msg",msg);
            map.put("soundOrVibrator",soundOrVibrator);
//            EventBus.getDefault().post(new EventMsg(0,map));
        }
        return Result.success();
    }
}
