package com.example.eventbus_api.manager;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.lifecycle.*;

import com.example.eventbus_annotations.EventBusBean;
import com.example.eventbus_api.EventBusMap;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EventBusManager implements LifecycleObserver{
    private volatile static EventBusManager mInstance;
    private List<Object> mRegisterList = new ArrayList<>();
    private LruCache<String, EventBusMap> mLruCache;
    private EventBusMap eventBusMap;
    private EventBusManager() {
        mLruCache = new LruCache<>(100);
    }

    public static EventBusManager getInstance() {
        if (mInstance == null) {
            synchronized (EventBusManager.class) {
                if (mInstance == null) {
                    mInstance = new EventBusManager();
                }
            }
        }
        return mInstance;
    }

    //注册这里 很简单 就是不停的往 mRegisterList 中添加你注册的对象
    public void register(Object clazz) {
        if (!mRegisterList.contains(clazz)) {
            mRegisterList.add(clazz);
        }
    }

    //反注册这里 很简单 就是不停的往 mRegisterList中移除你添加的对象
    public void unregister(Object clazz) {
        if (mRegisterList.contains(clazz)) {
            mRegisterList.remove(clazz);
        }
    }

    public int getAllObjectSize(){
        return mRegisterList.size();
    }

    /**
     * 发送消息
     * @param o 消息的数据
     */
    public void send(Object o) {
        try {
            Class<?> clazz;
            for (int i = 0; i < mRegisterList.size(); i++) {
                Object object = mRegisterList.get(i);
                clazz = object.getClass();
                if (eventBusMap == null) {
                    //这里才是反射创建对象
                    Log.e("PPS","  反射创建对象 EventBus$$APT");
                    String pathName = "com.pps.eventbus.EventBus$$APT" ;
                    Class<?> aClass = Class.forName(pathName);
                    eventBusMap = (EventBusMap) aClass.newInstance();
                }
                //从记录表中获取所当前class对象的方法的集合
                List<EventBusBean> eventBusBeans = eventBusMap.getClazzMethodMap().get(clazz);
                for (int j = 0; j < eventBusBeans.size(); j++) {
                    EventBusBean eventBusBean = eventBusBeans.get(j);
                    Class<?> parameterClazz = eventBusBean.getClazz();
                    if (getArgTypeClass(o.getClass()) == parameterClazz) {
                        if (eventBusBean.isMain() && (Looper.myLooper() != Looper.getMainLooper())){

                            /**
                             * (Looper.myLooper() != Looper.getMainLooper())  表示当前是处于子线程中
                             * 应为子线程不能更新ui 所有我就要把它切换到主线程中 怎么切换？
                             * 创建一个主线程的handler通过消息切换到主线程
                             */

                            //表示是当前的这个方法必须是主线程 但是现在是处于子线程中
                            Message message = Message.obtain();
                            message.what = 0x123;
                            message.obj = new EventManagerBean(clazz,eventBusBean,object,o);
                            mHandler.sendMessage(message);
                        }else {
                            /**
                             * 这里就是直接反射执行方法
                             */
                            Method method = clazz.getDeclaredMethod(eventBusBean.getMethodName(), parameterClazz);
                            method.setAccessible(true);
                            method.invoke(object, o);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理基本数据类型
     * @param clazz
     * @return
     */
    public Class getArgTypeClass(Class clazz) {
        if (Byte.class.equals(clazz)) {
            return byte.class;
        } else if (Short.class.equals(clazz)) {
            return short.class;
        } else if (Integer.class.equals(clazz)) {
            return int.class;
        } else if (Long.class.equals(clazz)) {
            return long.class;
        } else if (Float.class.equals(clazz)) {
            return float.class;
        } else if (Double.class.equals(clazz)) {
            return double.class;
        } else if (Boolean.class.equals(clazz)) {
            return boolean.class;
        } else if (Character.class.equals(clazz)) {
            return char.class;
        }
        return clazz;
    }


    /**
     * 创建主线程的Handler
     * 通过handler 来切换主线程
     */
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x123){
                EventManagerBean bean = (EventManagerBean) msg.obj;
                Method method = null;
                try {
                    method = bean.getClazz().getDeclaredMethod(bean.getEventBusBean().getMethodName(), bean.getEventBusBean().getClazz());
                    method.setAccessible(true);
                    method.invoke(bean.getMethodObject(), bean.getParameterObject());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    };


    /**
     * 这两个方法就是通过 lifecycler 来监听 Activity or Fragment的生命周期  这样呢 用户就不用去反注册了
     * 只需要在 Activity or Fragment中  getLifecycle().addObserver(EventBusManager.getInstance()); 添加监听就ok了
     * @param owner
     */

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreate(@NotNull LifecycleOwner owner){
        Log.e("PPS","onCreate  = " + owner.getClass());
        register(owner);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy(@NotNull LifecycleOwner owner){
        unregister(owner);
    }


}
