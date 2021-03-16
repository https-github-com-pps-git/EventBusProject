package com.example.eventbusproject.Test;

import android.app.Activity;
import android.util.Log;

import com.example.eventbusproject.MainActivity;

import java.lang.reflect.Method;

public class User {

    private String name = " 张三";
    private String sex = "男";
    private int age = 18;

    public User(String name,String sex,int age){
        this.name = name;
        this.sex = sex;
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", age=" + age +
                '}';
    }

    private void setName(String name, Activity activity)  {
        this.name = name;
        Log.e("PPS","设置的名称 = " + name);

        try {
            /**
             * 反射 MainActivity
             */
            Class mainClazz = MainActivity.class;
            Method fun1Method = mainClazz.getDeclaredMethod("set", String.class);
            fun1Method.setAccessible(true);
            fun1Method.invoke(activity,"哈哈哈哈");
        }catch (Exception e){
            e.printStackTrace();
        }



    }

}
