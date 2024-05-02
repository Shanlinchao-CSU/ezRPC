package com.example.rpcframework.client;

import com.example.rpcframework.annotation.DirectCall;
import com.example.rpcframework.config.RpcProperties;
import com.example.rpcframework.filter.FilterConfig;
import com.example.rpcframework.loadBalancer.balancerFactory;
import com.example.rpcframework.protocol.serialization.SerializationFactory;
import com.example.rpcframework.registry.RegistryFactory;
import com.example.rpcframework.utils.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * Consumer启动后执行的方法*/
@Slf4j
public class ClientBeanProcessor implements BeanPostProcessor, EnvironmentAware, InitializingBean {
    RpcProperties rpcProperties;
    /**
     * InitializingBean的方法重写
     * 调用时机:Bean属性被初始化完毕
     * 作用:初始化所有工厂,根据配置文件加载对应实现类*/
    @Override
    public void afterPropertiesSet() throws Exception {
        RegistryFactory.init();
        SerializationFactory.init();
        balancerFactory.init();
        FilterConfig.initClientFilter();
    }
    /**
     * EnvironmentAware的方法重写
     * 调用时机:Consumer启动
     * 作用:获取application.properties的配置信息,保存于environment参数*/
    @Override
    public void setEnvironment(Environment environment) {
        RpcProperties instance = RpcProperties.getInstance();
        PropertiesUtil.init(instance, environment);
        rpcProperties = instance;
        log.info("读取配置文件完毕");
    }
    /**
     * BeanPostProcessor的方法重写
     * 调用时机:Bean被加载完毕
     * 作用:处理所有被注册的Bean*/
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field:fields) {
            if (field.isAnnotationPresent(DirectCall.class)) {
                Class<?> type = field.getType();
                DirectCall directCall = field.getAnnotation(DirectCall.class);
                Object object = null;
                try {
                    object = Proxy.newProxyInstance(type.getClassLoader(),new Class<?>[]{type},
                            new ProxyHandler(directCall.serviceVersion(),directCall.overTime(),
                                    directCall.loadBalancerStrategy(),directCall.faultHandleStrategy(),
                                    directCall.retryTimes()));
                }catch (Exception e) {

                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
