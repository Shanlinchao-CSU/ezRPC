package com.example.rpcframework.filter;

import com.example.rpcframework.common.RpcRequest;
import com.example.rpcframework.common.RpcResponse;
import lombok.Data;

import java.util.Map;

@Data
public class FilterData {
    private String serviceVersion;
    private long timeout;
    private long retryCount;
    private String className;
    private String methodName;
    private Object args;
    private Map<String,Object> serviceAttachments;
    private Map<String,Object> clientAttachments;
    private RpcResponse data; // 执行业务逻辑后的数据

    public FilterData(RpcRequest request) {
        this.args = request.getData();
        this.className = request.getClassName();
        this.methodName = request.getMethodName();
        this.serviceVersion = request.getServiceVersion();
        this.serviceAttachments = request.getServiceAttachments();
        this.clientAttachments = request.getClientAttachments();
    }
}
