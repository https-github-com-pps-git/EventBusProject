package com.example.eventbusproject.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {


    public static void main(String[] args) {
        Map<Integer, List<String>> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add("张三");
        list.add("李四");
        list.add("王五");
        map.put(0, list);
        List<String> list1 = new ArrayList<>();
        list1.add("网红");
        list1.add("毛利");
        list1.add("狗屎");
        map.put(1, list1);

        for (int i = 0; i < map.size(); i++) {
            List<String> data = map.get(i);
            for (String datum : data) {
                System.out.println(" 获取的名字  =  " + datum);
            }
        }

    }
}
