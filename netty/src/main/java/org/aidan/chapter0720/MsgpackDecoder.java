package org.aidan.chapter0720;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

/**
 * MessagePack 解码器
 */
public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        final byte[] array;
        final int length = msg.readableBytes();
        array = new byte[length];
        // 获取需要解码的byte数组
        msg.getBytes(msg.readerIndex(), array, 0, length);
        MessagePack msgpack = new MessagePack();
        out.add(msgpack.read(array));
    }
}
