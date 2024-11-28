package com.yunshang.yunshang_reminder.service.impl;

import com.yunshang.yunshang_reminder.dao.ClockDao;
import com.yunshang.yunshang_reminder.entity.EventRemind;
import com.yunshang.yunshang_reminder.service.EventService;

import java.util.List;

public class EventServiceImpl implements EventService {

    private ClockDao dao = new ClockDao();
    @Override
    public Long add(EventRemind eventRemind) {

        return dao.add(eventRemind);
    }

    @Override
    public List<EventRemind> getAllClock() {

        return dao.getAllClock();
    }

    @Override
    public int update(String id,EventRemind remind) {
        return dao.update(id,remind);
    }

    @Override
    public EventRemind searchById(String id) {
        return dao.searchById(id);
    }

    @Override
    public int delClockById(String id) {
        return dao.delClockById(id);
    }

}
