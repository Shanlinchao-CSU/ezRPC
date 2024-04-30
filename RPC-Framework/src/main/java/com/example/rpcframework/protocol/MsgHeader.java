package com.example.rpcframework.protocol;

import lombok.Data;

@Data
public class MsgHeader {
    private short magic; //魔数
    private byte version; //协议版本号
    private byte msgType; //数据类型
    private byte status; //状态
    private long requestId; //请求 ID
    private int serializationLen; //序列化方法名长度
    private byte[] serialization; //序列化方法名
    private int msgLen; // 数据长度
}
