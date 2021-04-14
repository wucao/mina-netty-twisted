package com.xxg.network.lesson04;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * Created by wucao on 17/2/27.
 */
public class MyMinaEncoder extends ProtocolEncoderAdapter {

    @Override
    public void encode(IoSession session, Object message,
                       ProtocolEncoderOutput out) throws Exception {

        String msg = (String) message;
        byte[] bytes = msg.getBytes("UTF-8");
        int length = bytes.length;
        byte[] header = LittleEndian.toLittleEndian(length); // 按小字节序转成字节数组

        IoBuffer buffer = IoBuffer.allocate(length + 4);
        buffer.put(header); // header
        buffer.put(bytes); // body
        buffer.flip();
        out.write(buffer);
    }
}