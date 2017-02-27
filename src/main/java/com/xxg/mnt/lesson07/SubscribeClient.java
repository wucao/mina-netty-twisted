package com.xxg.mnt.lesson07;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by wucao on 17/2/27.
 */
public class SubscribeClient {

    public static void main(String[] args) throws IOException {

        Socket socket = null;
        BufferedReader in = null;

        try {

            socket = new Socket("localhost", 8080);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                String line = in.readLine(); // 阻塞等待服务器发布的消息
                if(line == null) {
                    break;
                }
                System.out.println(line);
            }

        } finally {
            // 关闭连接
            in.close();
            socket.close();
        }
    }
}
