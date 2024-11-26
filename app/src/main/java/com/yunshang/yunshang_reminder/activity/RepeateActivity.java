package com.yunshang.yunshang_reminder.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yunshang.yunshang_reminder.MyApplication;
import com.yunshang.yunshang_reminder.R;
import com.yunshang.yunshang_reminder.clock.ClockDiaAndSoundWithVibrator;
import com.yunshang.yunshang_reminder.entity.EventMsg;
import com.yunshang.yunshang_reminder.entity.SharedViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RepeateActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout repeate_once,repeate_everaDay,repeate_customize;
    private ImageView repeate_once_ima,repeate_everaDay_ima,repeate_customize_ima;
    private TextView repeate_customize_text;
    private int repeateId = 0;
    private ArrayList<Integer> customizeId = null;//周几的代号，1就是周一
    private String value = "只响一次";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeate);
        init();
        defaultBtn();
//        默认选择第一个
        repeate_once.setBackground(getResources().getDrawable(R.drawable.repeate_check));
        repeate_once_ima.setVisibility(View.VISIBLE);

    }

    private void init(){
        repeate_once = findViewById(R.id.repeate_once);
        repeate_everaDay = findViewById(R.id.repeate_everaDay);
        repeate_customize = findViewById(R.id.repeate_customize);
        repeate_once_ima = findViewById(R.id.repeate_once_ima);
        repeate_everaDay_ima = findViewById(R.id.repeate_everaDay_ima);
        repeate_customize_ima= findViewById(R.id.repeate_customize_ima);
        repeate_customize_text = findViewById(R.id.repeate_customize_text);

        repeate_once.setOnClickListener(this);
        repeate_everaDay.setOnClickListener(this);
        repeate_customize.setOnClickListener(this);
        repeate_once.setBackground(getResources().getDrawable(R.drawable.repeate_check));
        repeate_once_ima.setVisibility(View.VISIBLE);
    }
    private void defaultBtn(){
        repeate_once.setBackground(getResources().getDrawable(R.drawable.repeate));
        repeate_everaDay.setBackground(getResources().getDrawable(R.drawable.repeate));
        repeate_customize.setBackground(getResources().getDrawable(R.drawable.repeate));
        repeate_once_ima.setVisibility(View.INVISIBLE);
        repeate_everaDay_ima.setVisibility(View.INVISIBLE);
        repeate_customize_ima.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.repeate_once:
                defaultBtn();
                repeateId = 0;
                value = "只响一次";
                repeate_once.setBackground(getResources().getDrawable(R.drawable.repeate_check));
                repeate_once_ima.setVisibility(View.VISIBLE);

                break;
            case R.id.repeate_everaDay:
                defaultBtn();
                repeateId = 1;
                value = "每天";
                repeate_everaDay.setBackground(getResources().getDrawable(R.drawable.repeate_check));
                repeate_everaDay_ima.setVisibility(View.VISIBLE);
                break;
            case R.id.repeate_customize:

                initDialog();
                break;
        }
    }

    private void initDialog(){
        final String[] items5 = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};//创建item
        final boolean[] booleans = {false, false, false, false, false, false, false};
        AlertDialog alertDialog5 = new AlertDialog.Builder(this)
//                .setTitle("选择您喜欢的老湿")
//                .setIcon(R.mipmap.ic_launcher)
                .setMultiChoiceItems(items5, booleans, new DialogInterface.OnMultiChoiceClickListener() {//创建多选框
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        booleans[i] = b;
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder stringBuilder = new StringBuilder();
                        int a = 0;
                        if (customizeId == null)
                            customizeId = new ArrayList<>();
                        customizeId.clear();//确定时才清空
                        for (a = 0; a < booleans.length; a++) {
                            if (booleans[a]) {
                                stringBuilder.append(items5[a] + ",");
                                customizeId.add(a+1);//选择的天数id存入，取出时不用+1了
                            }
                        }
                        if (stringBuilder.length() <= 0)//什么也没选，就不做改变
                            return;
                        repeateId = 2;//选择的是自定义
                        defaultBtn();//按钮样式
                        stringBuilder.deleteCharAt(stringBuilder.length()-1);
                        value = stringBuilder.toString();
                        repeate_customize_text.setText(stringBuilder);
                        repeate_customize.setBackground(getResources().getDrawable(R.drawable.repeate_check));
                        repeate_customize_ima.setVisibility(View.VISIBLE);
                        Toast.makeText(RepeateActivity.this, "这是确定按钮" + "点的是：" + stringBuilder.toString(), Toast.LENGTH_SHORT).show();

                    }
                })

                .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(RepeateActivity.this, "这是取消按钮", Toast.LENGTH_SHORT).show();
                    }
                })
                .create();
        alertDialog5.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedViewModel model = new ViewModelProvider(MyApplication.getInstance()).get(SharedViewModel.class);
        model.setValue(value);
        model.setCustomizeId(customizeId);//是否为空都存入，接收方进行判断，空则不改
        model.setRepeateId(repeateId);

        Log.i("1221","选择的是：：："+value);
    }
}