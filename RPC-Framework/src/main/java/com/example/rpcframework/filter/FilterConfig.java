package com.example.rpcframework.filter;

import com.example.rpcframework.spi.SPILoader;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.IOException;

public class FilterConfig {

    // 服务端前置过滤器链
    @Getter
    private static FilterChain serverPreFilterChain = new FilterChain();
    // 服务端后置过滤器链
    @Getter
    private static FilterChain serverPostFilterChain = new FilterChain();
    // 客户端前置过滤器链
    @Getter
    private static FilterChain clientPreFilterChain = new FilterChain();
    // 客户端后置过滤器链
    @Getter
    private static FilterChain clientPostFilterChain = new FilterChain();

    @SneakyThrows // @SneakyThrows由lombok提供,为代码隐式添加了一个try-catch块
    public static void initServiceFilter(){
        final SPILoader spiLoader = SPILoader.getInstance();
        spiLoader.loadClasses(ServerPreFilter.class);
        spiLoader.loadClasses(ServerPostFilter.class);
        serverPreFilterChain.addFilter(spiLoader.getMap(ServerPreFilter.class));
        serverPostFilterChain.addFilter(spiLoader.getMap(ServerPostFilter.class));
    }

    @SneakyThrows
    public static void initClientFilter() throws IOException, ClassNotFoundException {
        final SPILoader spiLoader = SPILoader.getInstance();
        spiLoader.loadClasses(ClientPreFilter.class);
        spiLoader.loadClasses(ClientPostFilter.class);
        clientPreFilterChain.addFilter(spiLoader.getMap(ClientPreFilter.class));
        clientPostFilterChain.addFilter(spiLoader.getMap(ClientPostFilter.class));
    }
}
