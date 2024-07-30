package com.example.RpcFramework.protocol.serialization;

import com.example.RpcFramework.spi.ExtensionLoader;

/*序列化工厂*/
public class SerializationFactory {


    public static RpcSerialization get(String serialization) throws Exception {

        return ExtensionLoader.getInstance().get(serialization);

    }

    public static void init() throws Exception {
        ExtensionLoader.getInstance().loadExtension(RpcSerialization.class);
    }
}
