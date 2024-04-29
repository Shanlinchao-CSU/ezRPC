package com.example.provider01.ServiceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.provider01.mapper.RPCMapper;
import com.example.providersharing.entity.RPC;
import com.example.providersharing.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class testServiceImpl implements TestService {
    @Autowired
    RPCMapper rpcMapper;
    public RPC getRPC() {
        return rpcMapper.selectOne(new QueryWrapper<RPC>().eq("student_id",10));
    }
}
