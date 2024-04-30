package com.example.rpcframework.protocol.code;

import com.example.rpcframework.common.RpcMessage;
import com.example.rpcframework.common.RpcRequest;
import com.example.rpcframework.common.RpcResponse;
import com.example.rpcframework.common.constant.MsgType;
import com.example.rpcframework.common.constant.ProtocolConstant;
import com.example.rpcframework.config.RpcProperties;
import com.example.rpcframework.protocol.MsgHeader;
import com.example.rpcframework.protocol.RpcProtocol;
import com.example.rpcframework.protocol.serialization.SerializationFactory;
import com.example.rpcframework.protocol.serialization.SerializationService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class Decoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
     //如果可读长度小于协议头长度,那么说明没读完协议头,不能接收
     if (byteBuf.readableBytes() < ProtocolConstant.HEADER_TOTAL_LEN) {
         return;
     }
     //标记当前读下表,供回溯使用
     byteBuf.markReaderIndex();

     short magic = byteBuf.readShort();
     if (magic != ProtocolConstant.MAGIC) {
         throw new IllegalArgumentException("Valid false:magic number error!");
     }
     byte version = byteBuf.readByte();
     byte msgType = byteBuf.readByte();
     byte status = byteBuf.readByte();
     long requestID = byteBuf.readLong();
     int serializationLen = byteBuf.readInt();
     // 每次碰到长度不定的消息体,需要判断是否全部传输,这里是序列化方法名
     if (byteBuf.readableBytes() < serializationLen) {
         byteBuf.resetReaderIndex();
         return;
     }
     byte[] serialization_b = new byte[serializationLen];
     byteBuf.readBytes(serialization_b);
     String serialization = new String(serialization_b);

     int dataLen = byteBuf.readInt();
     if (byteBuf.readableBytes() < dataLen) {
         byteBuf.resetReaderIndex();
         return;
     }
     byte[] data = new byte[dataLen];
     byteBuf.readBytes(data);
     MsgType type = MsgType.findByType(msgType);
     if (type == null) {
         return;
     }
     RpcMessage rpcMessage;
     switch (type) {
         case REQUEST:
             rpcMessage  = SerializationFactory.get(serialization).deserialize(data, RpcRequest.class);
             break;
         case RESPONSE:
             rpcMessage = SerializationFactory.get(serialization).deserialize(data, RpcResponse.class);
             break;
         default:
             return;
     }

     MsgHeader header = new MsgHeader();
     header.setMagic(magic);
     header.setVersion(version);
     header.setStatus(status);
     header.setRequestId(requestID);

     if (rpcMessage != null) {
         RpcProtocol<RpcMessage> protocol = new RpcProtocol<>();
         protocol.setHeader(header);
         protocol.setBody(rpcMessage);
         list.add(protocol);
     }
    }
}
