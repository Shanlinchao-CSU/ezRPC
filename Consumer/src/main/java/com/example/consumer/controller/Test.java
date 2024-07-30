package com.example.consumer.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.RpcFramework.annotation.RpcReference;
import com.example.RpcFramework.common.constants.FaultTolerantRules;
import com.example.RpcFramework.common.constants.LoadBalancerRules;
import com.example.services.Test02Service;
import com.example.services.Test01Service;

@RestController
public class Test {

    @RpcReference(timeout = 10000L,faultTolerant = FaultTolerantRules.Failover)
    Test01Service testService;

    @RpcReference(loadBalancer = LoadBalancerRules.ConsistentHash)
    Test02Service test2Service;

    /**
     * 轮询
     * 会触发故障转移,提供方模拟异常
     * @param key
     * @return
     */
    @RequestMapping("test/{key}")
    public String test(@PathVariable String key){
        testService.test(key);
        return "test1 ok";
    }

    /**
     * 一致性哈希,无异常
     * @param key
     * @return
     */
    @RequestMapping("test2/{key}")
    public String test2(@PathVariable String key){
        return test2Service.test(key);
    }
}
