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
    public int update(EventRemind remind) {
        return dao.update(remind);
    }

}
