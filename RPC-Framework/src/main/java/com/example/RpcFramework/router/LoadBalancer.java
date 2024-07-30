package com.example.RpcFramework.router;

public interface LoadBalancer<T> {

     /**
      * 选择负载均衡策略
      * @param params 入参,可自定义拿到入参后自行处理负载策略
      * @param serviceName 服务key
      * @return 当前服务节点以及其他节点，用于给容错使用
      */
     ServiceMetaRes select(Object[] params,String serviceName);

}
