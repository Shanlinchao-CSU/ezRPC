package com.example.rpcframework.config;

import com.example.rpcframework.annotation.PropertyPrefix;
import com.example.rpcframework.annotation.PropertyField;
import com.example.rpcframework.common.constant.registryStrategies;
import com.example.rpcframework.common.constant.serializationStrategies;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * RPC框架配置类*/
@Data
@PropertyPrefix("RPC")
public class RpcProperties {
    // Netty Port
    @PropertyField
    private Integer nettyPort;

    // 注册中心具体实现方式
    @PropertyField
    private String registryMethod = registryStrategies.zooKeeper;

    // 注册中心地址
    @PropertyField
    private String registryAddress;

    // 注册中心密码
    @PropertyField
    private String registryPwd;

    // 序列化实现方式
    @PropertyField
    private String serializationMethod = serializationStrategies.JSON;

    // 服务端额外配置
    @PropertyField
    private Map<String,Object> serviceAttachments = new HashMap<>();

    // 客户端额外配置
    @PropertyField
    private Map<String,Object> clientAttachments = new HashMap<>();

    // 静态内部类实现单例
    private static class RPCPropertiesHolder {
        private static RpcProperties INSTANCE = new RpcProperties();
    }
    public static RpcProperties getInstance() {
        return RPCPropertiesHolder.INSTANCE;
    }

}
