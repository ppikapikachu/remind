package com.yunshang.yunshang_reminder;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

public class MyApplication extends Application implements ViewModelStoreOwner {

    private static class MyApplicationUtilHolder {
        private static final MyApplication Instance = new MyApplication();
    }

    public static MyApplication getInstance() {
        return MyApplicationUtilHolder.Instance;
    }


    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    //    全局获取Context
    public static Context getContext() {
        return context;
    }

    private ViewModelStore appViewModelStore ;
    private ViewModelStore getAppViewModelStore() {
        if (appViewModelStore == null) {
            appViewModelStore = new ViewModelStore();
        }
        return appViewModelStore;
    }
    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return getAppViewModelStore();
    }
}
