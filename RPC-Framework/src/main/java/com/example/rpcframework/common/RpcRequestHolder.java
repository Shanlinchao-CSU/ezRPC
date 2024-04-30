package com.example.rpcframework.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class RpcRequestHolder {
    public static final AtomicLong requestID = new AtomicLong(0);
    public static final Map<Long,RpcFuture<RpcResponse>> REQUEST_MAP = new ConcurrentHashMap<>();
}
