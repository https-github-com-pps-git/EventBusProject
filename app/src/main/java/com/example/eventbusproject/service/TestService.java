package com.example.eventbusproject.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventbus_api.manager.EventBusManager;
import com.example.eventbusproject.Test.User;

public class TestService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();


        EventBusManager.getInstance().send("大家好 我是卢本伟");

        mHandler.sendEmptyMessageDelayed(0x123,12000);
        mHandler.sendEmptyMessageDelayed(0x124,5000);

        new Thread(){
            @Override
            public void run() {
                super.run();

                try {
                    sleep(8000);
                    EventBusManager.getInstance().send(18);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x123){
                Log.e("PPS","接收到了消息" + EventBusManager.getInstance().getAllObjectSize());
                EventBusManager.getInstance().send(new User("卢本伟","男",30));
            }else  if (msg.what == 0x124){
                EventBusManager.getInstance().send(30);
            }
        }
    };
}
