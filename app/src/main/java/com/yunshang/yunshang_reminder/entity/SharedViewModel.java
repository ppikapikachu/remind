package com.yunshang.yunshang_reminder.entity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<Integer> repeateId = new MutableLiveData<>();//重复时间id
    private ArrayList<Integer> customizeId;//自定义时的选择天数
    private String value ;//重复时间内容

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
}
