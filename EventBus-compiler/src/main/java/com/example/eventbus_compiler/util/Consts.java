package com.example.eventbus_compiler.util;

public class Consts {

    //注解处理器 生成代码的包名
    public final static String APT_PACKAGE_NAME = "com.pps.eventbus";
    //EventBus注解的全类名
    public final static String EVENTBUS_PAGEAGE = "com.example.eventbus_annotations.EventBus";
    //生成文件的开头
    public final static String APT_START_NAME = "EventBus$$APT";
    //EventBusMap接口的全类名
    public final static String EVENTBUSMAP_NAME = "com.example.eventbus_api.EventBusMap";
    //EventBusMap 的方法的名字
    public final static String EVENTBUSMAP_METHOD_NAME = "getClazzMethodMap";
    //getMethodMap 方法中的list变量
    public final static String EVENTBUSMAP_METHOD_LIST = "list$";
    //getMethodMap 方法中的Map变量
    public final static String EVENTBUSMAP_METHOD_MAP = "mClazzMap";
}
