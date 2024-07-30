package com.example.povider.serviceImpl;

import com.example.services.Test01Service;
import org.springframework.stereotype.Service;

@Service
public class test01ServiceImpl implements Test01Service {
    @Override
    public void test(String key) {
        System.out.println("Provider01的测试方法01执行:"+key);
        throw new RuntimeException("故障转移测试");
    }
}
