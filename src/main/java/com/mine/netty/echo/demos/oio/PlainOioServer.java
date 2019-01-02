package com.mine.netty.echo.demos.oio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * PlainOioServer
 *
 * @author zhangsl
 * @date 2018/11/6 14:58
 */
public class PlainOioServer {
    public void server(int port) throws IOException {
        /**
         * 将服务器绑定到指定端口
         */
        ServerSocket socket = new ServerSocket(port);
        try {
            for (;;) {
                /**
                 * 接受连接
                 */
                Socket clientSocket = socket.accept();
                System.out.println(
                        "Accepted connection from " + clientSocket
                );
                /**
                 * 创建一个新的线程来处理该连接
                 */
                new Thread(() -> {
                    OutputStream out;
                    try {
                        out = clientSocket.getOutputStream();
                        /**
                         * 将消息写给已连接的客户端
                         */
                        out.write("Hi!\r\n".getBytes(Charset.forName("UTF-8")));
                        out.flush();
                        /**
                         * 关闭连接
                         */
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            clientSocket.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    /**
                     * 启动线程
                     */
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
