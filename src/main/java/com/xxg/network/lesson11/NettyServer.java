package com.xxg.network.lesson11;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.CharsetUtil;

import javax.net.ssl.SSLException;
import java.io.File;

/**
 * Created by wucao on 17/2/27.
 */
public class NettyServer {

    public static void main(String[] args) throws InterruptedException, SSLException {

        File certificate = new File("/Users/wucao/Desktop/ssl/cert.crt");  // 证书
        File privateKey = new File("/Users/wucao/Desktop/ssl/private.pem");  // 私钥
        final SslContext sslContext = SslContextBuilder.forServer(certificate, privateKey).build();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            // SslHandler要放在最前面
                            SslHandler sslHandler = sslContext.newHandler(ch.alloc());
                            pipeline.addLast(sslHandler);

                            pipeline.addLast(new LineBasedFrameDecoder(80));
                            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));

                            pipeline.addLast(new TcpServerHandler());
                        }
                    });
            ChannelFuture f = b.bind(8080).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}

class TcpServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String line = (String) msg;
        System.out.println("channelRead:" + line);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("channelActive");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("channelInactive");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}