package com.example.rpcframework.registry;

import com.example.rpcframework.spi.SPILoader;

import java.io.IOException;

public class RegistryFactory {
    public static RegistryService get(String serviceName) {
        return SPILoader.getInstance().get(serviceName);
    }
    public static void init() throws IOException, ClassNotFoundException {
        SPILoader.getInstance().loadClasses(RegistryService.class);
    }
}
