package com.xxg.network.lesson04;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

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
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            // 加上自己的Encoder和Decoder
                            pipeline.addLast(new MyNettyDecoder());
                            pipeline.addLast(new MyNettyEncoder());

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

    // 接收到新的数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        // MyNettyDecoder将接收到的数据由ByteBuf转为String
        String message = (String) msg;
        System.out.println("channelRead:" + message);

        // MyNettyEncoder将write的字符串添加了一个小字节序Header并转为字节码
        ctx.writeAndFlush("收到");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
