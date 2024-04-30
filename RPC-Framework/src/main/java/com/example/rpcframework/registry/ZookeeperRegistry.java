package com.example.rpcframework.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import com.example.rpcframework.common.Service;
import com.example.rpcframework.config.RpcProperties;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ZookeeperRegistry implements RegistryService {
    // 重试时间
    public static final int BASE_SLEEP_TIME_MS = 1000;
    // 重试次数
    public static final int MAX_RETRIES = 3;
    // 根路径
    public static final String ZK_BASE_PATH = "/slc_rpc";
    private final ServiceDiscovery<Service> serviceDiscovery;

    public ZookeeperRegistry() throws Exception {
        String registerAddr = RpcProperties.getInstance().getRegistryAddress();
        CuratorFramework client = CuratorFrameworkFactory.newClient(registerAddr, new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
        client.start();
        JsonInstanceSerializer<Service> serializer = new JsonInstanceSerializer<>(Service.class);
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(Service.class)
                .client(client)
                .serializer(serializer)
                .basePath(ZK_BASE_PATH)
                .build();
        this.serviceDiscovery.start();
    }

    @Override
    public void register(Service service) throws Exception {
        ServiceInstance<Service> serviceInstance = ServiceInstance
                .<Service>builder()
                .name(service.getServiceName()+"$"+service.getServiceVersion())
                .address(service.getServiceAddr())
                .port(service.getServicePort())
                .payload(service)
                .build();
        serviceDiscovery.registerService(serviceInstance);
    }

    @Override
    public void unRegister(Service service) throws Exception {
        ServiceInstance<Service> serviceInstance = ServiceInstance
                .<Service>builder()
                .name(service.getServiceName()+"$"+service.getServiceVersion())
                .address(service.getServiceAddr())
                .port(service.getServicePort())
                .payload(service)
                .build();
        serviceDiscovery.unregisterService(serviceInstance);
    }



    private List<Service> listServices(String serviceName) throws Exception {
        Collection<ServiceInstance<Service>> serviceInstances = serviceDiscovery.queryForInstances(serviceName);
        List<Service> ServiceInfos = serviceInstances.stream().map(ServiceInfoServiceInstance -> ServiceInfoServiceInstance.getPayload()).collect(Collectors.toList());
        return ServiceInfos;
    }
    @Override
    public List<Service> discover(String serviceName) {
        try {
            return listServices(serviceName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 关闭
     * @throws IOException
     */
    @Override
    public void destroy() throws IOException {
        serviceDiscovery.close();
    }

}

