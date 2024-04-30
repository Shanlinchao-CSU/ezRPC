package com.example.rpcframework.loadBalancer;

import com.example.rpcframework.common.Service;
import com.example.rpcframework.common.ServiceGroup;
import com.example.rpcframework.config.RpcProperties;
import com.example.rpcframework.registry.RegistryService;
import com.example.rpcframework.spi.SPILoader;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashBalancer implements loadBalancerService{
    private static final int VIRTUAL_NODE_SIZE = 10;
    @Override
    public ServiceGroup select(Object[] params, String serviceName) {
        // 注册中心
        RegistryService registryService = SPILoader.getInstance().get(RpcProperties.getInstance().getRegistryMethod());
        List<Service> discoveries = registryService.discover(serviceName);
        TreeMap<Integer,Service> ring = buildRing(discoveries);
        Service currentService = getCurrentService(ring,params[0].hashCode());
        return ServiceGroup.generateServiceGroup(currentService,discoveries);
    }
    private Service getCurrentService(TreeMap<Integer,Service> ring,int hashCode) {
        Map.Entry<Integer,Service> entry = ring.ceilingEntry(hashCode);
        if (entry == null) {
            entry = ring.firstEntry();
        }
        return entry.getValue();
    }

    /**
     * 构建一致性哈希环*/
    private TreeMap<Integer,Service> buildRing(List<Service> discoveries) {
        TreeMap<Integer,Service> ring = new TreeMap<>();
        for (Service service:discoveries) {
            for (int j=0;j<VIRTUAL_NODE_SIZE;j++) {
                // TreeMap有序
                ring.put((buildRingTag(service)+"$"+j).hashCode(),service);
            }
        }
        return ring;
    }

    /**
     * 根据Service信息构建能表示它的字符串*/
    private String buildRingTag(Service service) {
        return String.join(":",service.getServiceAddr(),String.valueOf(service.getServicePort()));
    }
}
