package com.example.eventbusproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.eventbus_annotations.EventBus;
import com.example.eventbus_api.manager.EventBusManager;
import com.example.eventbusproject.Test.User;


public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Log.e("PPS","注册前的个数  " + EventBusManager.getInstance().getAllObjectSize());
        getLifecycle().addObserver(EventBusManager.getInstance());
    }

    @EventBus
    private void setData(User user){
        Log.e("PPS","我接收到了消息 == " + user.toString());
    }

}