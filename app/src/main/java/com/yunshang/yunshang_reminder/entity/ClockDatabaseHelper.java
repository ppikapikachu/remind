package com.yunshang.yunshang_reminder.entity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class ClockDatabaseHelper extends SQLiteOpenHelper {
    // 创建一个Book表
    public static final String CREATE_CLOCK = "create table Clock ("
            + "id text, "
            + "repeateId integer, "
            + "createTime text, "
            + "startTime text, "
            + "status integer , "
            + "customizeId text, "//存json
            + "title text, "
            + "msg text)";
    public ClockDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "Clock.db", factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i("db创建：","创建成功");
        sqLiteDatabase.execSQL(CREATE_CLOCK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
