package com.example.rpcframework.client;

import com.example.rpcframework.common.RpcRequest;
import com.example.rpcframework.common.RpcRequestHolder;
import com.example.rpcframework.common.constant.MsgType;
import com.example.rpcframework.common.constant.ProtocolConstant;
import com.example.rpcframework.config.RpcProperties;
import com.example.rpcframework.filter.FilterConfig;
import com.example.rpcframework.filter.FilterData;
import com.example.rpcframework.protocol.MsgHeader;
import com.example.rpcframework.protocol.RpcProtocol;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Data
public class ProxyHandler implements InvocationHandler {
    String serviceVersion;
    long overTime;
    String loadBalancerStrategy;
    String faultHandleStrategy;
    long retryTimes;


    public ProxyHandler(String serviceVersion, long overTime, String loadBalancerStrategy, String faultHandleStrategy, long retryTimes) {
        this.serviceVersion = serviceVersion;
        this.overTime = overTime;
        this.loadBalancerStrategy = loadBalancerStrategy;
        this.faultHandleStrategy = faultHandleStrategy;
        this.retryTimes = retryTimes;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
        // 构建消息头
        MsgHeader header = new MsgHeader();
        header.setRequestId(RpcRequestHolder.requestID.incrementAndGet());
        header.setMagic(ProtocolConstant.MAGIC);
        header.setVersion(ProtocolConstant.VERSION);
        header.setMsgLen(ProtocolConstant.HEADER_TOTAL_LEN);
        byte[] serialization = RpcProperties.getInstance().getSerializationMethod().getBytes();
        header.setSerializationLen(serialization.length);
        header.setSerialization(serialization);
        header.setMsgType((byte) MsgType.REQUEST.ordinal());
        header.setStatus((byte) 0x1);
        protocol.setHeader(header);

        //构建请求体
        RpcRequest request = new RpcRequest();
        request.setServiceVersion(this.serviceVersion);
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setData(ObjectUtils.isEmpty(args) ? new Object[0] : args);
        request.setDataClass(ObjectUtils.isEmpty(args) ? null : args[0].getClass());
        request.setServiceAttachments(RpcProperties.getInstance().getServiceAttachments());
        request.setClientAttachments(RpcProperties.getInstance().getClientAttachments());
        protocol.setBody(request);

        //调用日志拦截器
        FilterData filterData = new FilterData(request);
        try {
            FilterConfig.getClientPreFilterChain().doFilter(filterData);
        }catch (Throwable e) {
            throw e;
        }


    }
}
