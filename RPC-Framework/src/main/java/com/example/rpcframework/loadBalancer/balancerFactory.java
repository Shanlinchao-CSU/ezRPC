package com.example.rpcframework.loadBalancer;

import com.example.rpcframework.spi.SPILoader;

import java.io.IOException;

public class balancerFactory {
    public static loadBalancerService get(String balancerName) {
        return SPILoader.getInstance().get(balancerName);
    }

    public static void init() throws IOException, ClassNotFoundException {
        SPILoader.getInstance().loadClasses(loadBalancerService.class);
    }
}
