package com.example.eventbusproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.eventbus_annotations.EventBus;
import com.example.eventbus_api.manager.EventBusManager;
import com.example.eventbusproject.Test.User;
import com.example.eventbusproject.service.TestService;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private Button mBtn;
    private TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //EventBusManager.getInstance().register(this);
        getLifecycle().addObserver(EventBusManager.getInstance());
        startService(new Intent(this, TestService.class));

        mBtn = findViewById(R.id.mBtn);
        mText = findViewById(R.id.mText);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EventBusManager.getInstance().send("我是自己写的EventBus");
                startActivity(new Intent(MainActivity.this, TestActivity.class));
            }
        });


    }


    @EventBus(isMainThread = true)
    public void setAge(int index){
        Log.e("PPS"," 当前是否是主线程  " + (Looper.myLooper() == Looper.getMainLooper()));
        Log.e("PPS", "我是MainActivity  setAge  message = " + index);
        mText.setText("我的年龄是" + index);
    }

    @EventBus
    private void fun1(String msg) {
        Log.e("PPS", "我是MainActivity  fun1  message = " + msg);
    }


    @EventBus
    private void setUser(User user) {
        Log.e("PPS", "我是MainActivity  setUser  message = " + user.toString());
        mText.setText(user.toString());
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 测试一下反射是怎么调用方法的
     */
    private void test() {


        try {

            /**
             * 反射User类
             */
            Class<User> userClass = User.class;
            User user = userClass.newInstance();
            Method setNameMethod = userClass.getDeclaredMethod("setName", String.class, Activity.class);
            setNameMethod.setAccessible(true);
            setNameMethod.invoke(user, "王麻子", this);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}