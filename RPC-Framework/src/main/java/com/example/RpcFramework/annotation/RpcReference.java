package com.example.RpcFramework.annotation;

import com.example.RpcFramework.common.constants.FaultTolerantRules;
import com.example.RpcFramework.common.constants.LoadBalancerRules;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RpcReference {
    String serviceVersion() default "1.0";
    long timeout() default 5000;
    String loadBalancer() default LoadBalancerRules.RoundRobin;
    String faultTolerant() default FaultTolerantRules.FailFast;
    long retryCount() default 3;
}
