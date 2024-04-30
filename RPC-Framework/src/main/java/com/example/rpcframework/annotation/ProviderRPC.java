package com.example.rpcframework.annotation;

import com.example.rpcframework.server.ProviderBeanProcessor;
import org.springframework.context.annotation.Import;
import java.lang.annotation.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 将ProviderBeanProcessor装入Bean
 * 开启Provider Bean的初始化Bean回调处理*/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(ProviderBeanProcessor.class)
public @interface ProviderRPC {
}
