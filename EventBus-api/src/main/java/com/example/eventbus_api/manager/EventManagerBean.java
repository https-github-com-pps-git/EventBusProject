package com.example.eventbus_api.manager;

import com.example.eventbus_annotations.EventBusBean;

public class EventManagerBean {
    private Class<?> clazz;//当前的class对象
    private EventBusBean eventBusBean;//发送消息的方法的信息
    private Object methodObject;//方法的对象
    private Object parameterObject;//参数的对象

    public EventManagerBean(Class<?> clazz, EventBusBean eventBusBean, Object methodObject, Object parameterObject) {
        this.clazz = clazz;
        this.eventBusBean = eventBusBean;
        this.methodObject = methodObject;
        this.parameterObject = parameterObject;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public EventBusBean getEventBusBean() {
        return eventBusBean;
    }

    public void setEventBusBean(EventBusBean eventBusBean) {
        this.eventBusBean = eventBusBean;
    }

    public Object getMethodObject() {
        return methodObject;
    }

    public void setMethodObject(Object methodObject) {
        this.methodObject = methodObject;
    }

    public Object getParameterObject() {
        return parameterObject;
    }

    public void setParameterObject(Object parameterObject) {
        this.parameterObject = parameterObject;
    }

    @Override
    public String toString() {
        return "EventManagerBean{" +
                "clazz=" + clazz +
                ", eventBusBean=" + eventBusBean +
                ", methodObject=" + methodObject +
                ", parameterObject=" + parameterObject +
                '}';
    }
}
