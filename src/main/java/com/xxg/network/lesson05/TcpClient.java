package com.xxg.network.lesson05;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 * Created by wucao on 17/2/27.
 */
public class TcpClient {

    public static void main(String[] args) throws IOException {

        Socket socket = null;
        DataOutputStream out = null;
        DataInputStream in = null;

        try {

            socket = new Socket("localhost", 8080);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            // 创建一个Student传给服务器
            StudentMsg.Student.Builder builder = StudentMsg.Student.newBuilder();
            builder.setId(1);
            builder.setName("客户端");
            builder.setEmail("xxg@163.com");
            builder.addFriends("A");
            builder.addFriends("B");
            StudentMsg.Student student = builder.build();
            byte[] outputBytes = student.toByteArray(); // Student转成字节码
            out.writeInt(outputBytes.length); // write header
            out.write(outputBytes); // write body
            out.flush();

            // 获取服务器传过来的Student
            int bodyLength = in.readInt();  // read header
            byte[] bodyBytes = new byte[bodyLength];
            in.readFully(bodyBytes);  // read body
            StudentMsg.Student student2 = StudentMsg.Student.parseFrom(bodyBytes); // body字节码解析成Student
            System.out.println("Header:" + bodyLength);
            System.out.println("Body:");
            System.out.println("ID:" + student2.getId());
            System.out.println("Name:" + student2.getName());
            System.out.println("Email:" + student2.getEmail());
            System.out.println("Friends:");
            List<String> friends = student2.getFriendsList();
            for(String friend : friends) {
                System.out.println(friend);
            }

        } finally {
            // 关闭连接
            in.close();
            out.close();
            socket.close();
        }
    }
}
