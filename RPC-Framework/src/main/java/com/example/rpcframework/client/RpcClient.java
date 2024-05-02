package com.example.rpcframework.client;

import com.example.rpcframework.common.RpcRequest;
import com.example.rpcframework.common.Service;
import com.example.rpcframework.protocol.RpcProtocol;
import com.example.rpcframework.protocol.code.Decoder;
import com.example.rpcframework.protocol.code.Encoder;
import com.example.rpcframework.protocol.handler.client.ResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;


/**
 * 处理客户端发送数据的类,涉及Netty相关知识*/
@Slf4j
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
                                .addLast(new Encoder())
                                .addLast(new Decoder())
                                .addLast(new ResponseHandler());
                    }
                });
    }
    public void sendRequest(RpcProtocol<RpcRequest> protocol, Service service) throws InterruptedException {
        if (service != null) {
            ChannelFuture future = bootstrap.connect(service.getServiceAddr(),service.getServicePort()).sync();
            future.addListener((ChannelFutureListener) listener -> {
                if (future.isSuccess()) {
                    log.info("连接服务{}:{}成功!",service.getServiceAddr(),service.getServicePort());
                    // 发送数据放在回调中,保证连接建立后再发送数据
                    future.channel().writeAndFlush(protocol);
                } else {
                    log.info("连接服务{}:{}失败!",service.getServiceAddr(),service.getServicePort());
                    //打印错误信息
                    future.cause().printStackTrace();
                    //将该客户端与服务端的连接优雅地关闭
                    eventLoopGroup.shutdownGracefully();
                }
            });
        }
    }
}
