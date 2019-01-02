package com.mine.netty.echo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class EchoServer {
    private int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 1) {
            System.err.println(
                    "Usage: " + EchoServer.class.getSimpleName() + " <port>"
            );
        }
        /**
         * 设置端口号
         */
        int port = Integer.parseInt(args[0]);
        /**
         * 调用服务器的start()方法
         */
        new EchoServer(port).start();
    }

    private void start() throws InterruptedException {
        EchoServerHandler serverHandler = new EchoServerHandler();
        /**
         * 创建EventLoopGroup
         */
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            /**
             * 创建SeverBootStrap
             */
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    /**
                     * 指定所使用的NIO传输通道
                     */
                    .channel(NioServerSocketChannel.class)
                    /**
                     * 使用指定的端口设置套接字地址
                     */
                    .localAddress(new InetSocketAddress(port))
                    /**
                     * 添加一个EchoServerHandler到子Channel的ChannelPipeline
                     */
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(serverHandler);
                        }
                    });
            /**
             * 异步地绑定服务器;调用sync()方法阻塞等待直到绑定完成
             */
            ChannelFuture f = b.bind().sync();
            /**
             * 获取Channel的CloseFuture,并且阻塞当前线程直到它完成
             */
            f.channel().closeFuture().sync();
        } finally {
            /**
             * 关闭EventLoopGroup,释放所有的资源
             */
            group.shutdownGracefully().sync();
        }
    }
}
