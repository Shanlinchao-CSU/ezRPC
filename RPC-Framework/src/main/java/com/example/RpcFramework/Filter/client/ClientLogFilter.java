package com.example.RpcFramework.Filter.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.RpcFramework.Filter.ClientBeforeFilter;
import com.example.RpcFramework.Filter.FilterData;

/*日志*/
public class ClientLogFilter implements ClientBeforeFilter {

    private Logger logger = LoggerFactory.getLogger(ClientLogFilter.class);


    @Override
    public void doFilter(FilterData filterData) {
        logger.info(filterData.toString());
    }
}
