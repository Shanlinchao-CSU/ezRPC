package com.example.rpcframework.utils;

import com.example.rpcframework.annotation.PropertyField;
import com.example.rpcframework.annotation.PropertyPrefix;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * 加载配置Util*/
public class PropertiesUtil {
    /**
     * 根据配置类字段的注解,在Environment中匹配值并赋给配置类实例*/
    public static void init(Object o, Environment environment) {
        final Class<?> aClass = o.getClass();
        final PropertyPrefix propertyPrefix = aClass.getAnnotation(PropertyPrefix.class);
        if (propertyPrefix == null) {
            throw new NullPointerException(aClass + " needs annotation: @PropertyPrefix");
        }
        String prefix = propertyPrefix.value();
        // 规定前缀注释规范
        if (prefix.endsWith(".")) {
            throw new RuntimeException("Annotation can't end with '.' : " + prefix);
        }
        for (Field field : aClass.getDeclaredFields()) {
            final PropertyField propertyField = field.getAnnotation(PropertyField.class);
            if (propertyField == null) continue;
            String fieldName = propertyField.value();
            // 如果字段的注释没有设置名字,那么直接使用该字段的变量名
            if (fieldName == null || fieldName.isEmpty()) {
                // 转小写,同时原大写间用"-"隔开
                fieldName = ConvertToLowerCase(field.getName());
            }
            fieldName = prefix + "." + fieldName;
            try {
                field.setAccessible(true);
                Class<?> type = field.getType();
                // 从environment中读取配置信息,查看是否有对应得上fieldName的,若有则取值赋给field
                Object result = bindSettingsToProperty(environment,type,fieldName);
                if (result == null) continue;
                field.set(o, result);
            }catch (Exception e) {
                e.printStackTrace();
            }
            field.setAccessible(false);
        }
    }

    private static String ConvertToLowerCase(String str) {
        StringBuilder builder = new StringBuilder();
        for (int i=0;i< str.length();i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                builder.append('-');
                builder.append(Character.toLowerCase(c));
            }else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    private static Object bindSettingsToProperty(Environment environment, Class<?> type, String fieldName) {
        try {
            Binder binder = Binder.get(environment);
            BindResult<?> bindResult = binder.bind(fieldName, type);
            return bindResult.get();
        }catch (NoSuchElementException e) {
            return null;
        }
    }
}
