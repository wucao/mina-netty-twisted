package com.xxg.mnt.lesson03;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.prefixedstring.PrefixedStringCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Created by wucao on 17/2/27.
 */
public class MinaServer {

    public static void main(String[] args) throws IOException {
        IoAcceptor acceptor = new NioSocketAcceptor();

        // 4字节的Header指定Body的字节数，对这种消息的处理
        acceptor.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(new PrefixedStringCodecFactory(Charset.forName("UTF-8"))));

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

        String msg = (String) message;
        System.out.println("messageReceived:" + msg);

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
