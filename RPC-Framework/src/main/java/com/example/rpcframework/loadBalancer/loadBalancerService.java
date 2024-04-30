package com.example.rpcframework.loadBalancer;

import com.example.rpcframework.common.ServiceGroup;

public interface loadBalancerService {
    ServiceGroup select(Object[] params, String serviceName);
}
