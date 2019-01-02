package com.mine.netty.echo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class EchoClient {
    private String host;
    private int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            /**
             * 创建BootStrap
             */
            Bootstrap b = new Bootstrap();
            /**
             * 指定EventLoopGroup以处理客户端时间;
             * 需要用于NIO的实现
             */
            b.group(group)
                    /**
                     * 适用于NIO传输的Channel类型
                     */
                    .channel(NioSocketChannel.class)
                    /**
                     * 设置服务器的IntSocketAddress
                     */
                    .remoteAddress(new InetSocketAddress(host, port))
                    /**
                     * 在创建Channel时,向ChannelPipeline中
                     * 添加一个EchoClientHandler实例
                     */
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new EchoClientHandler()
                            );
                        }
                    });
            /**
             * 连接到远程节点,阻塞等待直到连接完成
             */
            ChannelFuture f = b.connect().sync();
            /**
             * 阻塞,直到Channel关闭
             */
            f.channel().closeFuture().sync();
        } finally {
            /**
             * 关闭线程池并且释放所有的资源
             */
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 2) {
            System.err.println(
                    "Usage: " + EchoClient.class.getSimpleName() + " <host> <port>"
            );
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        new EchoClient(host, port).start();
    }
}
