package com.example.consumer.controller;

import com.example.providersharing.service.TestService;
import com.example.rpcframework.annotation.DirectCall;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.rpcframework.constant.faultHandleStrategies;
import com.example.rpcframework.constant.loadBalancerStrategies;
@RestController
public class Test {
    @DirectCall(overTime = 10000L, faultHandleStrategy = faultHandleStrategies.failOver, loadBalancerStrategy = loadBalancerStrategies.roundRobin)
    TestService testService;

    @RequestMapping("test/RPC")
    public String getRPC() {
        return testService.getRPC().toString();
    }
}
