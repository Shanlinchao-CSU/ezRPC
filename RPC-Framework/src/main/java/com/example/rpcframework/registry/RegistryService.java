package com.example.rpcframework.registry;

import com.example.rpcframework.common.Service;

import java.io.IOException;
import java.util.List;

public interface RegistryService {
    void register(Service serviceMeta) throws Exception;
    void unRegister(Service serviceMeta) throws Exception;
    List<Service> discover(String serviceName);
    void destroy() throws IOException;
}
