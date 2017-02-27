package com.xxg.mnt.lesson06;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by wucao on 17/2/27.
 */
public class TcpClient {

    public static void main(String[] args) throws IOException, InterruptedException {

        // 3次TCP连接，每个连接发送2个请求数据
        for(int i = 0; i < 3; i++) {


            Socket socket = null;
            OutputStream out = null;

            try {

                socket = new Socket("localhost", 8080);
                out = socket.getOutputStream();

                // 第一次请求服务器
                String lines1 = "Hello\r\n";
                byte[] outputBytes1 = lines1.getBytes("UTF-8");
                out.write(outputBytes1);
                out.flush();

                // 第二次请求服务器
                String lines2 = "World\r\n";
                byte[] outputBytes2 = lines2.getBytes("UTF-8");
                out.write(outputBytes2);
                out.flush();

            } finally {
                // 关闭连接
                out.close();
                socket.close();
            }

            Thread.sleep(1000);
        }
    }
}
