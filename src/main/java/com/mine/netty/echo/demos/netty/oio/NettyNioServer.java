package com.mine.netty.echo.demos.netty.oio;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * NettyNioServer
 *
 * @author zhangsl
 * @date 2018/11/7 10:22
 */
public class NettyNioServer {
    public void server(int port) throws Exception {
        final ByteBuf buf = Unpooled.copiedBuffer(
                "Hi" + System.getProperty("line.separator"), Charset.forName("UTF-8")
        );
        /**
         * 为非阻塞模式使用NioEventLoopGroup
         */
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group).channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    /**
                     * 指定ChannelInitializer,对于每个已接受的连接都调用它
                     */
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    /**
                                     * 添加ChannelInboundHandlerAdapter
                                     * 以接收和处理事件
                                     */
                                    new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            /**
                                             * 将消息写到客户端,并添加ChannelFutureListener,
                                             * 以便消息一被写完就关闭连接
                                             */
                                            ctx.writeAndFlush(buf.duplicate())
                                                    .addListener(ChannelFutureListener.CLOSE);
                                        }
                                    }
                            );
                        }
                    });
            /**
             * 绑定服务器,以接受连接
             */
            ChannelFuture future = b.bind().sync();
            future.channel().closeFuture().sync();
        } finally {
            /**
             * 释放所有的资源
             */
            group.shutdownGracefully().sync();
        }
    }
}
