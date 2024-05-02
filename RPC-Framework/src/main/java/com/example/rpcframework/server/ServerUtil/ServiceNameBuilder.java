package com.example.rpcframework.server.ServerUtil;

/**
 * 服务名构造器,服务注册和服务调用时都要用到,因此单独写一个类*/
public class ServiceNameBuilder {
    public static String buildServiceName(String serviceName,String serviceVersion) {
        return serviceName + "$" + serviceVersion;
    }
}
