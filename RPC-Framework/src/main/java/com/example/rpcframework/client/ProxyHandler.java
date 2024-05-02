package com.example.rpcframework.client;

import com.example.rpcframework.common.*;
import com.example.rpcframework.common.constant.MsgType;
import com.example.rpcframework.common.constant.ProtocolConstant;
import com.example.rpcframework.config.RpcProperties;
import com.example.rpcframework.filter.FilterConfig;
import com.example.rpcframework.filter.FilterData;
import com.example.rpcframework.loadBalancer.balancerFactory;
import com.example.rpcframework.loadBalancer.loadBalancerService;
import com.example.rpcframework.protocol.MsgHeader;
import com.example.rpcframework.protocol.RpcProtocol;
import com.example.rpcframework.server.ServerUtil.ServiceNameBuilder;
import com.example.rpcframework.common.constant.faultHandleStrategies;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Data
@Slf4j
public class ProxyHandler implements InvocationHandler {
    String serviceVersion;
    long overTime;
    String loadBalancerStrategy;
    String faultHandleStrategy;
    long maxRetryTimes;


    public ProxyHandler(String serviceVersion, long overTime, String loadBalancerStrategy, String faultHandleStrategy, long maxRetryTimes) {
        this.serviceVersion = serviceVersion;
        this.overTime = overTime;
        this.loadBalancerStrategy = loadBalancerStrategy;
        this.faultHandleStrategy = faultHandleStrategy;
        this.maxRetryTimes = maxRetryTimes;
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

        String serviceName = ServiceNameBuilder.buildServiceName(request.getClassName(),request.getServiceVersion());
        Object[] params = {request.getData()};
        loadBalancerService service = balancerFactory.get(loadBalancerStrategy);
        ServiceGroup group = service.select(params,serviceName);
        Service currentService = group.getCurrentService();
        Collection<Service> otherService = group.getOtherService();

        long retryTimes = 0,maxRetryTimes = this.maxRetryTimes;
        RpcClient client = new RpcClient();
        RpcResponse rpcResponse = null;

        // 重试机制
        while(retryTimes < maxRetryTimes) {
            RpcFuture<RpcResponse> future = new RpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()),overTime);
            // 保存改次请求信息
            RpcRequestHolder.REQUEST_MAP.put(header.getRequestId(), future);
            try {
                // 发送消息
                client.sendRequest(protocol, currentService);
                // 获取请求结果,在最后设置了超时参数
                rpcResponse = future.getPromise().get(future.getTimeout(), TimeUnit.MILLISECONDS);
                // 如果有异常并且没有其他服务
                if(rpcResponse.getException()!=null && otherService.size() == 0){
                    throw rpcResponse.getException();
                }
                if (rpcResponse.getException()!=null){
                    throw rpcResponse.getException();
                }
                log.info("调用服务{}成功!",serviceName);
                try {
                    FilterConfig.getClientPostFilterChain().doFilter(filterData);
                }catch (Throwable e){
                    throw e;
                }
                return rpcResponse.getData();
            }catch (Throwable e) {
                String errMsg = e.toString();
                switch (faultHandleStrategy) {
                    // 快速失败策略
                    case faultHandleStrategies.failFast:
                        log.error("RPC调用失败,触发快速失败,异常信息:{}",errMsg);
                        return rpcResponse.getException();
                    // 故障转移策略
                    case faultHandleStrategies.failOver:
                        log.warn("RPC调用失败,触发故障转移策略,第{}次重试,异常信息:{}",retryTimes+1,errMsg);
                        retryTimes++;
                        if (!ObjectUtils.isEmpty(otherService)){
                            currentService = otherService.iterator().next();
                            otherService.remove(currentService);
                        }else {
                            String error_show = String.format("RPC调用失败,{%s}无服务可用,异常信息:{%s}",serviceName,errMsg);
                            log.error(error_show);
                            throw new RuntimeException(error_show);
                        }
                        break;
                    // 忽略错误
                    case faultHandleStrategies.failSafe:
                        return null;
                }
            }
        }
        throw new RuntimeException("RPC调用失败,达到最大次数:"+maxRetryTimes);
    }
}
