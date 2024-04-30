package com.example.rpcframework.protocol.serialization;

import java.io.IOException;

public interface SerializationService {
    // 泛型类型函数,可以接收任何类型的参数
    <T> byte[] serialize(T obj) throws IOException;
    <T> T deserialize(byte[] data, Class<T> clazz) throws IOException;
}
