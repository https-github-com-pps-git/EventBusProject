package com.example.eventbus_api;

import com.example.eventbus_annotations.EventBusBean;

import java.util.List;
import java.util.Map;

public interface EventBusMap {

   /**
    * 获取所有带有EventBus注解的方法的集合
    * @return
    */
   Map<Class,List<EventBusBean>> getClazzMethodMap();
}
