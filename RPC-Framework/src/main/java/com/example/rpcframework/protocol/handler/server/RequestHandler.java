package com.example.rpcframework.protocol.handler.server;

import com.example.rpcframework.common.RpcRequest;
import com.example.rpcframework.protocol.RpcProtocol;
import com.example.rpcframework.thread_pool.ThreadPoolFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RequestHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcRequest> protocol) throws Exception {
        ThreadPoolFactory.submitRequest(channelHandlerContext,protocol);
    }
}
