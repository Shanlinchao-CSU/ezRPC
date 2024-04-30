package com.example.rpcframework.filter.server;

import com.example.rpcframework.config.RpcProperties;
import com.example.rpcframework.filter.FilterData;
import com.example.rpcframework.filter.ServerPreFilter;

import java.util.Map;

public class ValidateTokenFilter implements ServerPreFilter {
    @Override
    public void doFilter(FilterData filterData) {
        Map<String,Object> attachments = filterData.getClientAttachments();
        Map<String,Object> serverAttachments = RpcProperties.getInstance().getServiceAttachments();
        Object clientToken = attachments.get("token");
        Object serverToken = serverAttachments.get("token");
        if (clientToken==null || serverToken==null && clientToken.equals(serverToken)) {
            throw new IllegalArgumentException("token错误");
        }
    }
}
