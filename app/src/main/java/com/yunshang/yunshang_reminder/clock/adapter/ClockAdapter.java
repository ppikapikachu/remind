package com.yunshang.yunshang_reminder.clock.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.common.util.concurrent.ListenableFuture;
import com.yunshang.yunshang_reminder.MyApplication;
import com.yunshang.yunshang_reminder.R;
import com.yunshang.yunshang_reminder.activity.EditClock;
import com.yunshang.yunshang_reminder.clock.WorkManagerUtil;
import com.yunshang.yunshang_reminder.entity.EventMsg;
import com.yunshang.yunshang_reminder.entity.EventRemind;
import com.yunshang.yunshang_reminder.entity.SharedViewModel;
import com.yunshang.yunshang_reminder.service.EventService;
import com.yunshang.yunshang_reminder.service.impl.EventServiceImpl;
import com.yunshang.yunshang_reminder.utils.ConvertUtil;

import org.greenrobot.eventbus.EventBus;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ClockAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<EventRemind> data;
    private RecyclerView rv = null;
    private EventService eventService = new EventServiceImpl();
    public static final Integer TYPE_FOOTER = 100;

    public ClockAdapter(Context mContext, List<EventRemind> data) {
        this.mContext = mContext;
        this.data = data;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View itemview = LayoutInflater.from(mContext).inflate(R.layout.adapter_base, parent, false);
        RecyclerView.ViewHolder MyviewHolder = new MyViewHolder(itemview);

        return MyviewHolder;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        EventRemind remind = data.get(position);
        Log.i("适配器的remind:", remind + "");
        if (holder instanceof MyViewHolder) {
            Instant instant = Instant.ofEpochMilli(Long.valueOf(remind.getStartTime()));
            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
            int h = zonedDateTime.getHour();
            int m = zonedDateTime.getMinute();
            ((MyViewHolder) holder).clock_time.setText(formatHourMinute(h) + ":" + formatHourMinute(m));
            ((MyViewHolder) holder).clock_title.setText(remind.getTitle());
            String day = "";
            if (remind.getCustomizeId() != null) {//自定义
                for (Integer d : remind.getCustomizeId()) {
                    day = day + ConvertUtil.formateWeekDay(d);
                }
            } else {//只响一次或者每天响
                if (remind.getRepeateId() == 0)
                    day = day + "只响一次";
                else
                    day = day + "每天";
            }
            day = day + "| ";
            ((MyViewHolder) holder).clock_details.setText(day + remind.getMsg());
            ((MyViewHolder) holder).clock_status.setChecked(remind.getStatus() == 1 ? true : false);
        }

//        开启状态改变
        ((MyViewHolder) holder).clock_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = null;
                if (Math.abs(remind.getStatus()-1) == 1) {
                    if (remind.getRepeateId() == 0) {//启用时判断一次性任务还在不在workmanager中，不在就创建新的
                        ListenableFuture<WorkInfo> workInfoById = WorkManager.getInstance(MyApplication.getContext()).getWorkInfoById(UUID.fromString(remind.getId()));
                        WorkInfo.State state = null;
                        try {
                            state = workInfoById.get().getState();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
//                        状态为终止状态，重新创建
                        if (state == WorkInfo.State.FAILED || state == WorkInfo.State.SUCCEEDED || state == WorkInfo.State.CANCELLED) {
                            EventRemind remindReBuild = WorkManagerUtil.setWork(0, 0, Long.valueOf(remind.getCreateTime()), Long.valueOf(remind.getStartTime()),
                                    null, remind.getTitle(), remind.getMsg(), remind.getSoundOrboth());
                            id = String.valueOf(remindReBuild.getId());//新生成的任务的id

                        }
                    }
                }
//                改数据库的status，id不为空的话还会把id一起改了
                int update = eventService.update(id,new EventRemind(remind.getId(), null, null,
                        null, Math.abs(remind.getStatus()-1), null, null, null, null));
                remind.setStatus(Math.abs(remind.getStatus()-1));
                if (id!=null){
                    remind.setId(id);//赋值新id
                }
                EventBus.getDefault().post(new EventMsg(4,null));
                Log.i("update的结果：", update + "");
                Log.i("点击后刷新item：", remind + "");
            }
        });
        //在适配器中直接给itemView设置点击事件，然后进行条目的操作。
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                传入原值
                new ViewModelProvider(MyApplication.getInstance()).get(SharedViewModel.class).setEventRemind(remind);
                Intent intent = new Intent(mContext, EditClock.class);
                mContext.startActivity(intent);
                return false;
            }
        });

    }


    //返回数据长度
    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView clock_time;
        TextView clock_title;
        TextView clock_details;
        Switch clock_status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.clock_time = itemView.findViewById(R.id.clock_time);
            this.clock_title = itemView.findViewById(R.id.clock_title);
            this.clock_details = itemView.findViewById(R.id.clock_details);
            this.clock_status = itemView.findViewById(R.id.clock_status);

        }

    }

    private String formatHourMinute(int i) {
        if (i < 10)
            return "0" + i;
        return i + "";
    }


}
