package com.example.rpcframework.protocol.handler.server;

import com.example.rpcframework.common.RpcRequest;
import com.example.rpcframework.common.RpcResponse;
import com.example.rpcframework.common.constant.MsgStatus;
import com.example.rpcframework.filter.FilterConfig;
import com.example.rpcframework.filter.FilterData;
import com.example.rpcframework.protocol.MsgHeader;
import com.example.rpcframework.protocol.RpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostFilterHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcRequest> protocol) throws Exception {
        RpcRequest request = protocol.getBody();
        MsgHeader header = protocol.getHeader();
        long request_id = header.getRequestId();
        RpcResponse response = new RpcResponse();
        FilterData filterData = new FilterData(request);
        try {
            FilterConfig.getServerPostFilterChain().doFilter(filterData);
        }catch (Exception e) {
            response.setException(e);
            header.setStatus((byte) MsgStatus.FAILED.ordinal());
            log.error("PostFilter of Request{} error:{}",request_id,e.toString());
        }
        channelHandlerContext.writeAndFlush(protocol);
    }
}
