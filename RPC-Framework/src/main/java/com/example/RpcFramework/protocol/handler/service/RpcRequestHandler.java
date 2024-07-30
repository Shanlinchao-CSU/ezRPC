package com.example.RpcFramework.protocol.handler.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import com.example.RpcFramework.common.RpcRequest;
import com.example.RpcFramework.poll.ThreadPollFactory;
import com.example.RpcFramework.protocol.RpcProtocol;

/*提供方消息处理器*/
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {


    public RpcRequestHandler() {}

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) {
        ThreadPollFactory.submitRequest(ctx,protocol);
    }

}

