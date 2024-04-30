package com.example.providersharing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/*用于测试的,对应数据库表的类*/
@Data
@TableName(value="rpc")
public class RPC {
    private int student_id;
}
