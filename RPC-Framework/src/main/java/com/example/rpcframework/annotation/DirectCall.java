package com.example.rpcframework.annotation;

import com.example.rpcframework.constant.loadBalancerStrategies;
import com.example.rpcframework.constant.faultHandleStrategies;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 打在Consumer直接调用的对象上的注解
 * 用于提示将该对象替换为代理对象*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DirectCall {
    String serviceVersion() default "1.0";
    long overTime() default 5000;
    String loadBalancerStrategy() default loadBalancerStrategies.consistentHashing;
    String faultHandleStrategy() default faultHandleStrategies.failFast;
    long retryTimes() default 3;
}