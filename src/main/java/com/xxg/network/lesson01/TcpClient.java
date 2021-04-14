package com.xxg.network.lesson01;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by wucao on 17/2/27.
 */
public class TcpClient {

    public static void main(String[] args) throws IOException, InterruptedException {

        Socket socket = null;
        OutputStream out = null;
        InputStream in = null;

        try{

            socket = new Socket("localhost", 8080);
            out = socket.getOutputStream();
            in = socket.getInputStream();

            // 请求服务器
            out.write("第一次请求".getBytes("UTF-8"));
            out.flush();

            // 获取服务器响应，输出
            byte[] byteArray = new byte[1024];
            int length = in.read(byteArray);
            System.out.println(new String(byteArray, 0, length, "UTF-8"));

            Thread.sleep(5000);

            // 再次请求服务器
            out.write("第二次请求".getBytes("UTF-8"));
            out.flush();

            // 再次获取服务器响应，输出
            byteArray = new byte[1024];
            length = in.read(byteArray);
            System.out.println(new String(byteArray, 0, length, "UTF-8"));


        } finally {
            // 关闭连接
            in.close();
            out.close();
            socket.close();
        }

    }
}
