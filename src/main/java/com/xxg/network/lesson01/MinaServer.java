package com.xxg.network.lesson01;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by wucao on 17/2/27.
 */
public class MinaServer {

    public static void main(String[] args) throws IOException {
        IoAcceptor acceptor = new NioSocketAcceptor();
        acceptor.setHandler(new TcpServerHandle());
        acceptor.bind(new InetSocketAddress(8080));
    }
}

class TcpServerHandle extends IoHandlerAdapter {

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    // 接收到新的数据
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {

        // 接收客户端的数据
        IoBuffer ioBuffer = (IoBuffer) message;
        byte[] byteArray = new byte[ioBuffer.limit()];
        ioBuffer.get(byteArray, 0, ioBuffer.limit());
        System.out.println("messageReceived:" + new String(byteArray, "UTF-8"));

        // 发送到客户端
        byte[] responseByteArray = "你好".getBytes("UTF-8");
        IoBuffer responseIoBuffer = IoBuffer.allocate(responseByteArray.length);
        responseIoBuffer.put(responseByteArray);
        responseIoBuffer.flip();
        session.write(responseIoBuffer);
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        System.out.println("sessionCreated");
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        System.out.println("sessionClosed");
    }
}