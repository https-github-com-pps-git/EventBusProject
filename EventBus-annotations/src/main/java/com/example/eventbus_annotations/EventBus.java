package com.example.eventbus_annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface EventBus {
    boolean isMainThread() default false;//表示是否一定需要在主线程中执行
}
