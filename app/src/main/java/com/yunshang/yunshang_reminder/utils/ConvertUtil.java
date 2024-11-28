package com.yunshang.yunshang_reminder.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class ConvertUtil {

    public static String formateWeekDay(int i) {
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
