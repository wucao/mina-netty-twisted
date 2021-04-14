package com.xxg.network.lesson11;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Created by wucao on 17/2/27.
 */
public class MinaServer {

    public static void main(String[] args) throws Exception {


        String certPath = "/Users/wucao/Desktop/ssl/cert.crt";  // 证书
        String privateKeyPath = "/Users/wucao/Desktop/ssl/private.der";  // 私钥

        // 证书
        // https://docs.oracle.com/javase/7/docs/api/java/security/cert/X509Certificate.html
        InputStream inStream = null;
        Certificate certificate = null;
        try {
            inStream = new FileInputStream(certPath);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            certificate = cf.generateCertificate(inStream);
        } finally {
            if (inStream != null) {
                inStream.close();
            }
        }

        // 私钥
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Files.readAllBytes(new File(privateKeyPath).toPath()));
        PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);

        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        Certificate[] certificates = {certificate};
        ks.setKeyEntry("key", privateKey, "".toCharArray(), certificates);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, "".toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);

        IoAcceptor acceptor = new NioSocketAcceptor();
        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
        chain.addLast("ssl", new SslFilter(sslContext));  // SslFilter需要放在最前面
        chain.addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"), "\r\n", "\r\n")));
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

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
        String line = (String) message;
        System.out.println("messageReceived:" + line);
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