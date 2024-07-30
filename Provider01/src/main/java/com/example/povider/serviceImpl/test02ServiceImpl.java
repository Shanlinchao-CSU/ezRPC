package com.example.povider.serviceImpl;

import com.example.services.Test02Service;

public class test02ServiceImpl implements Test02Service {
    @Override
    public String test(String key) {
        System.out.println("Provider01的测试方法02执行:"+key);
        return "test ok";
    }
}
