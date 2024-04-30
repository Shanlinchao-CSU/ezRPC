package com.example.rpcframework.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class Service implements Serializable {
    private String serviceName;
    private String serviceVersion;
    private String serviceAddr;
    private int servicePort;
    /**
     * Redis
     */
    private long endTime;
    private String UUID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service that = (Service) o;
        return servicePort == that.servicePort &&
                serviceName.equals(that.serviceName) &&
                serviceVersion.equals(that.serviceVersion) &&
                serviceAddr.equals(that.serviceAddr) &&
                UUID.equals(that.UUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceName, serviceVersion, serviceAddr, servicePort, UUID);
    }
}
