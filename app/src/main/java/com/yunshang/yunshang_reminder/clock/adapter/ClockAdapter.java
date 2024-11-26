package com.yunshang.yunshang_reminder.clock.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.yunshang.yunshang_reminder.R;
import com.yunshang.yunshang_reminder.entity.EventRemind;
import com.yunshang.yunshang_reminder.service.EventService;
import com.yunshang.yunshang_reminder.service.impl.EventServiceImpl;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
                    day  = day+formateWeekDay(d);
                }
            }else {//只响一次或者每天响
                if (remind.getRepeateId() == 0)
                    day = day + "只响一次";
                else
                    day = day + "每天";
            }
            day = day+"| ";
            ((MyViewHolder) holder).clock_details.setText(day+remind.getMsg());
            ((MyViewHolder) holder).clock_status.setChecked(remind.getStatus() == 1 ? true : false);
        }
        ((MyViewHolder) holder).clock_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int update = eventService.update(new EventRemind(remind.getId(), null, null, null
                        , Math.abs(remind.getStatus() - 1), null, null, null));
                Log.i("update的结果：", update + "");
            }
        });
        //在适配器中直接给itemView设置点击事件，然后进行条目的操作。
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

//                Intent intent = new Intent(mContext, PostDetail.class);
//
//                //发送点击条目的条目信息给下一个活动，以便获取对应位置的数据进行显示
//                Bundle bundle = new Bundle();
//                Posts posts1 = null;
//                //因为用data.get(position)获取的对象没有序列化，get方法没有提供序列化，所以只能自己重新创建一次
//                //这里bitmap是没有提供序列化的类，所以不能直接传含bitmap属性的实例化对象
////                posts1 = new Posts(null, posts.getId(), posts.getTitle(), posts.getContext(), posts.getUpusername(), posts.getNickname(), posts.getImgpath());
//
//                //不再用bitmap了，所以也不需要再做手动序列化bitmap了
//                bundle.putSerializable("listpost", posts);
//                intent.putExtra("bundlefromadapter", bundle);
//                mContext.startActivity(intent);
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

    private String formateWeekDay(int i) {
        if (i == 1)
            return "周一 ";
        if (i == 2)
            return "周二 ";
        if (i == 3)
            return "周三 ";
        if (i == 4)
            return "周四 ";
        if (i == 5)
            return "周五 ";
        if (i == 6)
            return "周六 ";
        if (i == 7)
            return "周日 ";
        return "";
    }
}
