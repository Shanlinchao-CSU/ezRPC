package com.example.rpcframework.common.constant;

public enum MsgType {
    REQUEST,
    RESPONSE,
    HEARTBEAT;

    public static MsgType findByType(int type) {
        return MsgType.values()[type];
    }
}
