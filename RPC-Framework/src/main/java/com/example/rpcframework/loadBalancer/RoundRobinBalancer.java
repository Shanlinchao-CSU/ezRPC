package com.example.rpcframework.loadBalancer;

import com.example.rpcframework.common.Service;
import com.example.rpcframework.common.ServiceGroup;
import com.example.rpcframework.config.RpcProperties;
import com.example.rpcframework.registry.RegistryFactory;
import com.example.rpcframework.registry.RegistryService;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 这种方法不适用于服务频繁变动的情况*/
public class RoundRobinBalancer implements loadBalancerService{
    private static final AtomicInteger RRID = new AtomicInteger(0);
    @Override
    public ServiceGroup select(Object[] params, String serviceName) {
        // 注册中心
        RegistryService registryService = RegistryFactory.get(RpcProperties.getInstance().getRegistryMethod());
        // 所有服务
        List<Service> discoveries = registryService.discover(serviceName);
        int size = discoveries.size();
        RRID.addAndGet(1);
        if (RRID.get() == Integer.MAX_VALUE){
            RRID.set(0);
        }
        // 轮询,从所有服务中分别取值
        return ServiceGroup.generateServiceGroup(discoveries.get(RRID.get() % size),discoveries);
    }
}
