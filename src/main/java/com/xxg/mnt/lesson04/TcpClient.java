package com.xxg.mnt.lesson04;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by wucao on 17/2/27.
 */
public class TcpClient {

    public static void main(String[] args) throws IOException {

        Socket socket = null;
        OutputStream out = null;
        InputStream in = null;

        try {

            socket = new Socket("localhost", 8080);
            out = socket.getOutputStream();
            in = socket.getInputStream();

            // 请求服务器
            String data = "我是客户端";
            byte[] outputBytes = data.getBytes("UTF-8");
            out.write(LittleEndian.toLittleEndian(outputBytes.length)); // write header
            out.write(outputBytes); // write body
            out.flush();

            // 获取响应
            byte[] inputBytes = new byte[1024];
            int length = in.read(inputBytes);
            if(length >= 4) {
                int bodyLength = LittleEndian.getLittleEndianInt(inputBytes);
                if(length >= 4 + bodyLength) {
                    byte[] bodyBytes = Arrays.copyOfRange(inputBytes, 4, 4 + bodyLength);
                    System.out.println("Header:" + bodyLength);
                    System.out.println("Body:" + new String(bodyBytes, "UTf-8"));
                }
            }

        } finally {
            // 关闭连接
            in.close();
            out.close();
            socket.close();
        }
    }
}
