package com.xxg.mnt.lesson05;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import java.util.List;

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

                            // 负责通过4字节Header指定的Body长度将消息切割
                            pipeline.addLast("frameDecoder",
                                    new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));

                            // 负责将frameDecoder处理后的完整的一条消息的protobuf字节码转成Student对象
                            pipeline.addLast("protobufDecoder",
                                    new ProtobufDecoder(StudentMsg.Student.getDefaultInstance()));

                            // 负责将写入的字节码加上4字节Header前缀来指定Body长度
                            pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));

                            // 负责将Student对象转成protobuf字节码
                            pipeline.addLast("protobufEncoder", new ProtobufEncoder());

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

        // 读取客户端传过来的Student对象
        StudentMsg.Student student = (StudentMsg.Student) msg;
        System.out.println("ID:" + student.getId());
        System.out.println("Name:" + student.getName());
        System.out.println("Email:" + student.getEmail());
        System.out.println("Friends:");
        List<String> friends = student.getFriendsList();
        for(String friend : friends) {
            System.out.println(friend);
        }

        // 新建一个Student对象传到客户端
        StudentMsg.Student.Builder builder = StudentMsg.Student.newBuilder();
        builder.setId(9);
        builder.setName("服务器");
        builder.setEmail("123@abc.com");
        builder.addFriends("X");
        builder.addFriends("Y");
        StudentMsg.Student student2 = builder.build();
        ctx.writeAndFlush(student2);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
