package com.example.rpcframework.server;

import com.example.rpcframework.annotation.RpcService;
import com.example.rpcframework.common.Service;
import com.example.rpcframework.config.RpcProperties;
import com.example.rpcframework.filter.FilterConfig;
import com.example.rpcframework.loadBalancer.balancerFactory;
import com.example.rpcframework.protocol.code.Decoder;
import com.example.rpcframework.protocol.code.Encoder;
import com.example.rpcframework.protocol.handler.server.PostFilterHandler;
import com.example.rpcframework.protocol.handler.server.PreFilterHandler;
import com.example.rpcframework.protocol.handler.server.RequestHandler;
import com.example.rpcframework.protocol.serialization.SerializationFactory;
import com.example.rpcframework.registry.RegistryFactory;
import com.example.rpcframework.registry.RegistryService;
import com.example.rpcframework.server.ServerUtil.ServiceNameBuilder;
import com.example.rpcframework.thread_pool.ThreadPoolFactory;
import com.example.rpcframework.utils.PropertiesUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务端*/
@Slf4j
public class ServerBeanProcessor implements BeanPostProcessor, EnvironmentAware, InitializingBean {
    private RpcProperties rpcProperties;
    private String serverAddress = "localhost";
    private final Map<String,Object> rpcServiceMap = new HashMap<>();

    @Override
    public void setEnvironment(Environment environment) {
        RpcProperties properties = RpcProperties.getInstance();
        PropertiesUtil.init(properties,environment);
        rpcProperties = properties;
        log.info("读取配置文件完毕");
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        // 找到bean上带有 RpcService 注解的类
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if (rpcService != null) {
            // 可能会有多个接口,默认选择第一个接口,这里的beanClass.getInterfaces()就是ServiceSharing/TestService
            String serviceName = beanClass.getInterfaces()[0].getName();
            if (!rpcService.serviceInterface().equals(void.class)){
                serviceName = rpcService.serviceInterface().getName();
            }
            String serviceVersion = rpcService.serviceVersion();
            try {
                // 服务注册
                int servicePort = rpcProperties.getNettyPort();
                // 获取注册中心 ioc
                RegistryService registryService = RegistryFactory.get(rpcProperties.getRegistryMethod());
                Service service = new Service();
                // 服务提供方地址
                service.setServiceAddr("localhost");
                service.setServicePort(servicePort);
                service.setServiceVersion(serviceVersion);
                service.setServiceName(serviceName);
                registryService.register(service);
                // 缓存
                rpcServiceMap.put(ServiceNameBuilder.buildServiceName(service.getServiceName(),service.getServiceVersion()), bean);
                log.info("成功注册服务:{}${}",serviceName,serviceVersion);
            } catch (Exception e) {
                log.error("服务{}${}注册失败!失败原因:{}",serviceName,serviceVersion,e.toString());
            }
        }
        return bean;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            startRpcServer();
        }catch (InterruptedException e) {
            log.error("服务端启动RPC失败!");
            return;
        }
        SerializationFactory.init();
        RegistryFactory.init();
        balancerFactory.init();
        FilterConfig.initServiceFilter();
        ThreadPoolFactory.setServiceMap(rpcServiceMap);
    }

    private void startRpcServer() throws InterruptedException{
        int serverPort = rpcProperties.getNettyPort();
        EventLoopGroup worker = new NioEventLoopGroup();
        EventLoopGroup boss = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss,worker)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new Encoder())
                                    .addLast(new Decoder())
                                    .addLast(new PreFilterHandler())
                                    .addLast(new RequestHandler())
                                    .addLast(new PostFilterHandler());
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE,true);

            ChannelFuture channelFuture = bootstrap.bind(this.serverAddress, serverPort).sync();
            log.info("Server start on {}:{}", this.serverAddress, serverPort);
            channelFuture.channel().closeFuture().sync();
            // 确保JVM关闭时能关闭服务
            Runtime.getRuntime().addShutdownHook(new Thread(() ->
            {
                boss.shutdownGracefully();
                log.info("BossGroup has been shutdown");
                worker.shutdownGracefully();
                log.info("WorkerGroup has been shutdown");
            }, "Allen-thread"));
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
