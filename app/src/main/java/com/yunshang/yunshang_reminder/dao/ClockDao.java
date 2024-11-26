package com.yunshang.yunshang_reminder.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yunshang.yunshang_reminder.MyApplication;
import com.yunshang.yunshang_reminder.entity.ClockDatabaseHelper;
import com.yunshang.yunshang_reminder.entity.EventRemind;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ClockDao {

    ClockDatabaseHelper helper = new ClockDatabaseHelper(MyApplication.getContext(),null,null,1);

    public long add(EventRemind event){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id",event.getId());
        values.put("repeateId",event.getRepeateId());
        values.put("createTime",event.getCreateTime());
        values.put("startTime",event.getStartTime());
        values.put("status",event.getStatus());
        Gson gson = new Gson();
        String customizeId = gson.toJson(event.getCustomizeId());//json化
        values.put("customizeId",customizeId);
        values.put("title",event.getTitle());
        values.put("msg",event.getMsg());

        long insert = db.insert("Clock",null,values);
        values.clear();
        db.close();
        return insert;
    }

    @SuppressLint({"Recycle", "Range"})
    public List<EventRemind> getAllClock(){
        List<EventRemind> reminds = new ArrayList<EventRemind>();
        SQLiteDatabase db = helper.getWritableDatabase();
//        查询所有数据
        Cursor cursor = db.query("Clock", null, null, null, null, null, null);
        if (cursor != null)
        while (cursor.moveToNext()){
            EventRemind e = new EventRemind();
            e.setId(cursor.getString(cursor.getColumnIndex("id")));
            e.setRepeateId(cursor.getInt(cursor.getColumnIndex("repeateId")));
            e.setCreateTime(cursor.getString(cursor.getColumnIndex("createTime")));
            e.setStartTime(cursor.getString(cursor.getColumnIndex("startTime")));
            e.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
//            json转集合
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Integer>>() {}.getType();
            List<Integer> list = gson.fromJson(cursor.getString(cursor.getColumnIndex("customizeId")), listType);
            e.setCustomizeId((ArrayList<Integer>) list);
            e.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            e.setMsg(cursor.getString(cursor.getColumnIndex("msg")));

            reminds.add(e);
        }
        cursor.close();
        db.close();
        return reminds;
    }

    public int update(EventRemind remind){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (remind.getRepeateId() != null)
            values.put("repeateId",remind.getRepeateId());
        if (remind.getStartTime() != null)
            values.put("startTime",remind.getStartTime());
        if (remind.getStatus() != null)
            values.put("status",remind.getStatus());
        if (remind.getCustomizeId() != null){
            Gson gson = new Gson();
            String customizeId = gson.toJson(remind.getCustomizeId());//json化
            values.put("customizeId",customizeId);
        }
        if (remind.getTitle() != null)
            values.put("title",remind.getTitle());
        if (remind.getMsg() != null)
            values.put("msg",remind.getMsg());
        int clock = db.update("Clock", values, "id = ?", new String[]{remind.getId()});
        db.close();
        return clock;
    }
}
