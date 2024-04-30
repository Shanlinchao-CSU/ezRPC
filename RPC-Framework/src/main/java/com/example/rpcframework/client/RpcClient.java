package com.example.rpcframework.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


/**
 * 处理客户端发送数据的类,涉及Netty相关知识*/
public class RpcClient {
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    public RpcClient() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast()
                                .addLast()
                                .addLast();
                    }
                });
    }
}
