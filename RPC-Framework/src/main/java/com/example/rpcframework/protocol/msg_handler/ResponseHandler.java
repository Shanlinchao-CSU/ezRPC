package com.example.rpcframework.protocol.msg_handler;

import com.example.rpcframework.common.RpcResponse;
import com.example.rpcframework.protocol.RpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ResponseHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcResponse> rpcResponseRpcProtocol) throws Exception {

    }
}
