package com.example.eventbus_annotations;

import java.io.Serializable;

public class EventBusBean implements Serializable {

    private String methodName;//方法的名字

    private Class<?> clazz;//参数的类型

    private boolean isMain;
    public EventBusBean(){

    }

    public EventBusBean(String methodName,Class<?> clazz,boolean isMainThread){
        this.clazz = clazz;
        this.methodName = methodName;
        this.isMain = isMainThread;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    @Override
    public String toString() {
        return "EventBusBean{" +
                "methodName='" + methodName + '\'' +
                ", clazz=" + clazz +
                ", isMain=" + isMain +
                '}';
    }
}
