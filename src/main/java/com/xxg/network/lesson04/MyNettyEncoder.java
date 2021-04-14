package com.xxg.network.lesson04;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by wucao on 17/2/27.
 */
public class MyNettyEncoder extends MessageToByteEncoder<String> {

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out)
            throws Exception {

        byte[] bytes = msg.getBytes("UTF-8");
        int length = bytes.length;
        byte[] header = LittleEndian.toLittleEndian(length); // int按小字节序转字节数组
        out.writeBytes(header); // write header
        out.writeBytes(bytes); // write body
    }
}