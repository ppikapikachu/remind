package com.yunshang.yunshang_reminder.service;

import com.yunshang.yunshang_reminder.entity.EventRemind;

import java.util.List;

public interface EventService {

    public Long add(EventRemind eventRemind);//添加一个

    public List<EventRemind> getAllClock();//查询所有

    public int update(String id,EventRemind remind);//更新一条数据

    public EventRemind searchById(String id);//根据id查一条

    public int delClockById(String id);//根据id删除一条
}
