package com.example.rpcframework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记配置类属性字段*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PropertyField {
    String value() default "";
}
