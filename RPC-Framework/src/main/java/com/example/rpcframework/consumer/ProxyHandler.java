package com.example.rpcframework.consumer;

import lombok.Data;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Data
public class ProxyHandler implements InvocationHandler {
    String serviceVersion;
    long overTime;
    String loadBalancerStrategy;
    String faultHandleStrategy;
    long retryTimes;


    public ProxyHandler(String serviceVersion, long overTime, String loadBalancerStrategy, String faultHandleStrategy, long retryTimes) {
        this.serviceVersion = serviceVersion;
        this.overTime = overTime;
        this.loadBalancerStrategy = loadBalancerStrategy;
        this.faultHandleStrategy = faultHandleStrategy;
        this.retryTimes = retryTimes;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
