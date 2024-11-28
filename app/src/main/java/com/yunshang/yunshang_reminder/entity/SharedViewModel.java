package com.yunshang.yunshang_reminder.entity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<Integer> repeateId = new MutableLiveData<>();//重复类型id
    private ArrayList<Integer> customizeId;//自定义时的选择天数集合
    private String value ;//重复时间内容
    private EventRemind eventRemind;//编辑时用来传递

    public LiveData<Integer> getRepeateId() {
        return repeateId;
    }

    public void setRepeateId(Integer id) {
        repeateId.setValue(id);
    }

    public ArrayList<Integer> getCustomizeId() {
        return customizeId;
    }

    public void setCustomizeId(ArrayList<Integer> customizeId) {
        this.customizeId = customizeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public EventRemind getEventRemind() {
        return eventRemind;
    }

    public void setEventRemind(EventRemind eventRemind) {
        this.eventRemind = eventRemind;
    }

    public void reset(){
        repeateId = new MutableLiveData<>();
        customizeId = null;
        value = null;
        eventRemind = null;
    }
}
