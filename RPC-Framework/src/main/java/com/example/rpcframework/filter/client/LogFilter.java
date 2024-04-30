package com.example.rpcframework.filter.client;

import com.example.rpcframework.filter.ClientPreFilter;
import com.example.rpcframework.filter.FilterData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogFilter implements ClientPreFilter {
    @Override
    public void doFilter(FilterData filterData) {
        log.info(filterData.toString());
    }
}
