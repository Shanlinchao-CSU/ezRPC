package com.example.rpcframework.protocol.serialization;

import com.example.rpcframework.spi.SPILoader;

import java.io.IOException;

public class SerializationFactory {
    public static SerializationService get(String strategy) {
        return SPILoader.getInstance().get(strategy);
    }
    public static void init() throws IOException, ClassNotFoundException {
        SPILoader.getInstance().loadClasses(SerializationService.class);
    }
}
