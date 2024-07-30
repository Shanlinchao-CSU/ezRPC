package com.example.RpcFramework.Filter.service;

import com.example.RpcFramework.Filter.FilterData;
import com.example.RpcFramework.Filter.ServiceBeforeFilter;
import com.example.RpcFramework.config.RpcProperties;

import java.util.Map;

/*token拦截器*/
public class ServiceTokenFilter implements ServiceBeforeFilter {

    @Override
    public void doFilter(FilterData filterData) {
        final Map<String, Object> attachments = filterData.getClientAttachments();
        final Map<String, Object> serviceAttachments = RpcProperties.getInstance().getServiceAttachments();
        if (!attachments.getOrDefault("token","").equals(serviceAttachments.getOrDefault("token",""))){
            throw new IllegalArgumentException("token不正确");
        }
    }

}
