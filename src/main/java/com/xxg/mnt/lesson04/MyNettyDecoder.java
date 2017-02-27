package com.xxg.mnt.lesson04;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by wucao on 17/2/27.
 */
public class MyNettyDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        // 如果没有接收完Header部分（4字节），直接退出该方法
        if(in.readableBytes() >= 4) {

            // 标记开始位置，如果一条消息没传输完成则返回到这个位置
            in.markReaderIndex();

            byte[] bytes = new byte[4];
            in.readBytes(bytes); // 读取4字节的Header

            int bodyLength = LittleEndian.getLittleEndianInt(bytes); // header按小字节序转int

            // 如果body没有接收完整
            if(in.readableBytes() < bodyLength) {
                in.resetReaderIndex(); // ByteBuf回到标记位置
            } else {
                byte[] bodyBytes = new byte[bodyLength];
                in.readBytes(bodyBytes);
                String body = new String(bodyBytes, "UTF-8");
                out.add(body); // 解析出一条消息
            }
        }
    }
}