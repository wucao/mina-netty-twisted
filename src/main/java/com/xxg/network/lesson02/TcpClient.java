package com.xxg.network.lesson02;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by wucao on 17/2/27.
 */
public class TcpClient {

    public static void main(String[] args) throws IOException, InterruptedException {

        Socket socket = null;
        OutputStream out = null;

        try{

            socket = new Socket("localhost", 8080);
            out = socket.getOutputStream();

            String lines = "床前";
            byte[] outputBytes = lines.getBytes("UTF-8");
            out.write(outputBytes);
            out.flush();

            Thread.sleep(1000);

            lines = "明月";
            outputBytes = lines.getBytes("UTF-8");
            out.write(outputBytes);
            out.flush();

            Thread.sleep(1000);

            lines = "光\r\n疑是地上霜\r\n举头";
            outputBytes = lines.getBytes("UTF-8");
            out.write(outputBytes);
            out.flush();

            Thread.sleep(1000);

            lines = "望明月\r\n低头思故乡\r\n";
            outputBytes = lines.getBytes("UTF-8");
            out.write(outputBytes);
            out.flush();

        } finally {
            // 关闭连接
            out.close();
            socket.close();
        }

    }
}
