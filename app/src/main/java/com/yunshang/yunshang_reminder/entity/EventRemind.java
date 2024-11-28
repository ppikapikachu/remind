package com.yunshang.yunshang_reminder.entity;

import java.util.ArrayList;

public class EventRemind {
    private String id;//本条事件的id,可以用当前时间
    private Integer repeateId;//重复时间id,0一次 1每天 2自定义
    private String createTime;//创建时间
    private String startTime;//响铃时间
    private Integer status;//开启状态,0关1开
    private ArrayList<Integer> customizeId;//自定义时的选择天数
    private String title;//事件名
    private String msg;//事件描述
    private Integer soundOrBoth;//响铃或加上震动

    public EventRemind() {
    }

    public EventRemind(String id, Integer repeateId, String createTime, String startTime, Integer status, ArrayList<Integer> customizeId, String title, String msg,Integer soundOrBoth) {
        this.id = id;
        this.repeateId = repeateId;
        this.createTime = createTime;
        this.startTime = startTime;
        this.status = status;
        this.customizeId = customizeId;
        this.title = title;
        this.msg = msg;
        this.soundOrBoth = soundOrBoth;
    }

    public EventRemind(Integer repeateId, String startTime, ArrayList<Integer> customizeId, String title, String msg,Integer soundOrBoth) {
        this.repeateId = repeateId;
        this.startTime = startTime;
        this.customizeId = customizeId;
        this.title = title;
        this.msg = msg;
        this.soundOrBoth = soundOrBoth;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getRepeateId() {
        return repeateId;
    }

    public void setRepeateId(Integer repeateId) {
        this.repeateId = repeateId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public ArrayList<Integer> getCustomizeId() {
        return customizeId;
    }

    public void setCustomizeId(ArrayList<Integer> customizeId) {
        this.customizeId = customizeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getSoundOrboth() {
        return soundOrBoth;
    }

    public void setSoundOrboth(Integer soundOrBoth) {
        this.soundOrBoth = soundOrBoth;
    }

    @Override
    public String toString() {
        return "EventRemind{" +
                "id='" + id + '\'' +
                ", repeateId=" + repeateId +
                ", createTime='" + createTime + '\'' +
                ", startTime='" + startTime + '\'' +
                ", status=" + status +
                ", customizeId=" + customizeId +
                ", title='" + title + '\'' +
                ", msg='" + msg + '\'' +
                ", soundOrBoth=" + soundOrBoth +
                '}';
    }
}
