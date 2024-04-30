package com.example.rpcframework.registry;

import com.example.rpcframework.common.Service;

import java.io.IOException;
import java.util.List;

/**
 * Redis服务注册中心尚未实现
 */
public class RedisRegistry implements RegistryService {
    @Override
    public void register(Service serviceMeta) throws Exception {

    }

    @Override
    public void unRegister(Service serviceMeta) throws Exception {

    }

    @Override
    public List<Service> discover(String serviceName) {
        return null;
    }

    @Override
    public void destroy() throws IOException {

    }
}
