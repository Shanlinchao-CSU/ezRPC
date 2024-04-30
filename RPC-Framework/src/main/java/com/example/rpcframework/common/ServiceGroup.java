package com.example.rpcframework.common;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class ServiceGroup {
    private Service currentService;
    private Collection<Service> otherService;

    public static ServiceGroup generateServiceGroup(Service service, Collection<Service> otherService) {
        final ServiceGroup serviceGroup = new ServiceGroup();
        serviceGroup.setCurrentService(service);
        if (otherService.size() == 1) {
            serviceGroup.setOtherService(new ArrayList<>());
        }else {
            otherService.remove(service);
            serviceGroup.setOtherService(otherService);
        }
        return serviceGroup;
    }
}
