package com.example.rpcframework.protocol.serialization;

import com.caucho.hessian.io.HessianSerializerInput;
import com.caucho.hessian.io.HessianSerializerOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerialization implements SerializationService{
    @Override
    public <T> byte[] serialize(T object) {
        if (object == null) {
            throw new NullPointerException();
        }
        HessianSerializerOutput hessianOutput;
        // 该写法确保了ByteArrayOutputStream无论有无异常均会被关闭
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            hessianOutput = new HessianSerializerOutput(os);
            hessianOutput.writeObject(object);
            hessianOutput.flush();
            return os.toByteArray();
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }

    // 该注释确保执行时不会出现check相关警告
    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (bytes == null) {
            throw new NullPointerException();
        }
        try (ByteArrayInputStream is = new ByteArrayInputStream(bytes)) {
            HessianSerializerInput hessianInput = new HessianSerializerInput(is);
            return (T) hessianInput.readObject(clazz);
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }
}
