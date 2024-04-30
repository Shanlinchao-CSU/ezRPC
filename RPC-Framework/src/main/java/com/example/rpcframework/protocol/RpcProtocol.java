package com.example.rpcframework.protocol;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcProtocol<T> implements Serializable {
    private MsgHeader header;
    private T body;
}
