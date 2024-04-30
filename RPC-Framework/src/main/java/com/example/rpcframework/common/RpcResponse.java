package com.example.rpcframework.common;

import lombok.Data;

@Data
public class RpcResponse extends RpcMessage{
    private Object data;
    private Class dataClass;
    private String message;
    private Exception exception;
}
