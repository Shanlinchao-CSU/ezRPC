package com.example.provider02.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.services.Test01Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class test01ServiceImpl implements Test01Service {

    @Override
    public void test(String key) {
        System.out.println("Provider02的测试方法01执行:"+key);
    }
}
