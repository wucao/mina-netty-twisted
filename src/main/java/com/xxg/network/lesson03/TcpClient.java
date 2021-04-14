package com.xxg.network.lesson03;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by wucao on 17/2/27.
 */
public class TcpClient {

    public static void main(String[] args) throws IOException {

        Socket socket = null;
        DataOutputStream out = null;

        try {

            socket = new Socket("localhost", 8080);
            out = new DataOutputStream(socket.getOutputStream());

            // 请求服务器
            String data1 = "牛顿";
            byte[] outputBytes1 = data1.getBytes("UTF-8");
            out.writeInt(outputBytes1.length); // write header
            out.write(outputBytes1); // write body

            String data2 = "爱因斯坦";
            byte[] outputBytes2 = data2.getBytes("UTF-8");
            out.writeInt(outputBytes2.length); // write header
            out.write(outputBytes2); // write body

            out.flush();

        } finally {
            // 关闭连接
            out.close();
            socket.close();
        }

    }
}
