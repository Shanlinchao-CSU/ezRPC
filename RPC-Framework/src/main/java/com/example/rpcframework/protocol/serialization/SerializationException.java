package com.example.rpcframework.protocol.serialization;

public class SerializationException extends RuntimeException{
    private static final long serializationVersionUID = -1;

    public SerializationException() {
        super();
    }
    public SerializationException(String msg) {
        super(msg);
    }
    public SerializationException(String msg, Throwable cause) {
        super(msg, cause);
    }
    public SerializationException(Throwable cause) {
        super(cause);
    }
}
