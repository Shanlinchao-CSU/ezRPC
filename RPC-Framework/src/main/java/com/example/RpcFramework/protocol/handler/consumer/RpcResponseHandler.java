package com.example.RpcFramework.protocol.handler.consumer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import com.example.RpcFramework.common.RpcFuture;
import com.example.RpcFramework.common.RpcRequestHolder;
import com.example.RpcFramework.common.RpcResponse;
import com.example.RpcFramework.protocol.RpcProtocol;

/*消费方消息处理器*/
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcResponse> msg) {
        long requestId = msg.getHeader().getRequestId();
        RpcFuture<RpcResponse> future = RpcRequestHolder.REQUEST_MAP.remove(requestId);
        future.getPromise().setSuccess(msg.getBody());
    }

}
