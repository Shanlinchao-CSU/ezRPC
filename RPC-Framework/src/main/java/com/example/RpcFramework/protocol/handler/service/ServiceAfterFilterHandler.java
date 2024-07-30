package com.example.RpcFramework.protocol.handler.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.RpcFramework.Filter.FilterConfig;
import com.example.RpcFramework.Filter.FilterData;
import com.example.RpcFramework.Filter.client.ClientLogFilter;
import com.example.RpcFramework.common.RpcResponse;
import com.example.RpcFramework.common.constants.MsgStatus;
import com.example.RpcFramework.protocol.MsgHeader;
import com.example.RpcFramework.protocol.RpcProtocol;

/*后置拦截器*/
public class ServiceAfterFilterHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

    private Logger logger = LoggerFactory.getLogger(ClientLogFilter.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcResponse> protocol) {
        final FilterData filterData = new FilterData();
        filterData.setData(protocol.getBody());
        RpcResponse response = new RpcResponse();
        MsgHeader header = protocol.getHeader();
        try {
            FilterConfig.getServiceAfterFilterChain().doFilter(filterData);
        } catch (Exception e) {
            header.setStatus((byte) MsgStatus.FAILED.ordinal());
            response.setException(e);
            logger.error("after process request {} error", header.getRequestId(), e);
        }
        ctx.writeAndFlush(protocol);
    }
}
