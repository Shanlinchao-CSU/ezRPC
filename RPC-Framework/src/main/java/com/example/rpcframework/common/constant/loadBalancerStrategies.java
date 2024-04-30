package com.example.rpcframework.common.constant;

/*接口中的变量会被隐式指定为public static final,因此这里选择接口而不是抽象类*/
public interface loadBalancerStrategies {
    String consistentHashing = "consistentHashing";
    String roundRobin = "roundRobin";
}
