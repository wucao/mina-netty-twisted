package com.xxg.mnt.lesson12;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.http.HttpServerCodec;
import org.apache.mina.http.api.*;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wucao on 17/2/27.
 */
public class MinaServer {

    public static void main(String[] args) throws Exception {

        String certPath = "/Users/wucao/Desktop/https/1_gw2.vsgames.cn_bundle.crt";  // 证书
        String privateKeyPath = "/Users/wucao/Desktop/https/private.der";  // 私钥

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
        chain.addLast("ssl", new SslFilter(sslContext));  // SslFilter + HttpServerCodec实现HTTPS
        chain.addLast("codec", new HttpServerCodec());
        acceptor.setHandler(new HttpServerHandle());
        acceptor.bind(new InetSocketAddress(8080));
    }
}

class HttpServerHandle extends IoHandlerAdapter {

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {

        if (message instanceof HttpRequest) {

            // 请求，解码器将请求转换成HttpRequest对象
            HttpRequest request = (HttpRequest) message;

            // 获取请求参数
            String name = request.getParameter("name");
            if(name == null) {
                name = "World";
            }
            name = URLDecoder.decode(name, "UTF-8");

            // 响应HTML
            String responseHtml = "<html><body>Hello, " + name + "</body></html>";
            byte[] responseBytes = responseHtml.getBytes("UTF-8");
            int contentLength = responseBytes.length;

            // 构造HttpResponse对象，HttpResponse只包含响应的status line和header部分
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "text/html; charset=utf-8");
            headers.put("Content-Length", Integer.toString(contentLength));
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SUCCESS_OK, headers);

            // 响应BODY
            IoBuffer responseIoBuffer = IoBuffer.allocate(contentLength);
            responseIoBuffer.put(responseBytes);
            responseIoBuffer.flip();

            session.write(response); // 响应的status line和header部分
            session.write(responseIoBuffer); // 响应body部分
        }
    }
}