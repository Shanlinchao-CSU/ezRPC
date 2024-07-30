package com.example.RpcFramework.annotation;

import org.springframework.context.annotation.Import;
import com.example.RpcFramework.provider.ProviderPostProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*开启服务提供方自动装配*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(ProviderPostProcessor.class)
public @interface EnableProviderRpc {

}
