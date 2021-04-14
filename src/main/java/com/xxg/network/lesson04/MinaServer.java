package com.xxg.network.lesson04;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by wucao on 17/2/27.
 */
public class MinaServer {

    public static void main(String[] args) throws IOException {
        IoAcceptor acceptor = new NioSocketAcceptor();

        // 指定编码解码器
        acceptor.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(new MyMinaEncoder(), new MyMinaDecoder()));

        acceptor.setHandler(new TcpServerHandle());
        acceptor.bind(new InetSocketAddress(8080));
    }
}

class TcpServerHandle extends IoHandlerAdapter {

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        cause.printStackTrace();
    }

    // 接收到新的数据
    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {

        // MyMinaDecoder将接收到的数据由IoBuffer转为String
        String msg = (String) message;
        System.out.println("messageReceived:" + msg);

        // MyMinaEncoder将write的字符串添加了一个小字节序Header并转为字节码
        session.write("收到");
    }
}