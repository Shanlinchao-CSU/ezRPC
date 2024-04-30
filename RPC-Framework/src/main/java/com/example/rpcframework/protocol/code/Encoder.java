package com.example.rpcframework.protocol.code;

import com.example.rpcframework.protocol.MsgHeader;
import com.example.rpcframework.protocol.RpcProtocol;
import com.example.rpcframework.protocol.serialization.SerializationFactory;
import com.example.rpcframework.protocol.serialization.SerializationService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class Encoder extends MessageToByteEncoder<RpcProtocol<Object>> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcProtocol<Object> objectRpcProtocol, ByteBuf byteBuf) throws Exception {
        MsgHeader header = objectRpcProtocol.getHeader();
        // 写入魔数(安全校验，可以参考java中的CAFEBABE)
        byteBuf.writeShort(header.getMagic());
        byteBuf.writeByte(header.getVersion());
        byteBuf.writeByte(header.getMsgType());
        byteBuf.writeByte(header.getStatus());
        // 写入请求id(请求id可以用于记录异步回调标识,具体需要回调给哪个请求)
        byteBuf.writeLong(header.getRequestId());
        // 写入序列化方式(接收方需要依靠具体哪个序列化进行序列化)
        byteBuf.writeInt(header.getSerializationLen());
        final byte[] ser = header.getSerialization();
        final String serialization = new String(ser);
        byteBuf.writeBytes(ser);
        SerializationService serializationService = SerializationFactory.get(serialization);
        byte[] data = serializationService.serialize(objectRpcProtocol.getBody());
        // 写入数据长度(接收方根据数据长度读取数据内容)
        byteBuf.writeInt(data.length);
        // 写入数据
        byteBuf.writeBytes(data);
    }
}
