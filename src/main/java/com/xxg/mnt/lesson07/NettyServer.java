package com.xxg.mnt.lesson07;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Created by wucao on 17/2/27.
 */
public class NettyServer {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LineBasedFrameDecoder(80));
                            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
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

    // ChannelGroup用于保存所有连接的客户端，注意要用static来保证只有一个ChannelGroup实例，否则每new一个TcpServerHandler都会创建一个ChannelGroup
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        channels.add(ctx.channel()); // 将新的连接加入到ChannelGroup，当连接断开ChannelGroup会自动移除对应的Channel
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        channels.writeAndFlush(msg + "\r\n"); // 接收到消息后，将消息发送到ChannelGroup中的所有客户端
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // cause.printStackTrace();  暂时把异常打印注释掉，因为PublishClient发布一条消息后会立即断开连接，而服务器也会向PublishClient发送消息，所以会抛出异常
        ctx.close();
    }
}