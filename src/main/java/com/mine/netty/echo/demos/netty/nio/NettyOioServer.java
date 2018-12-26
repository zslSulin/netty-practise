package com.mine.netty.echo.demos.netty.nio;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * NettyOioServer
 *
 * @author zhangsl
 * @date 2018/11/7 10:01
 */
public class NettyOioServer {
    public void server(int port) throws Exception {
        final ByteBuf buf = Unpooled.unreleasableBuffer(
                Unpooled.copiedBuffer("Hi!" + System.getProperty("line.separator"), Charset.forName("UTF-8"))
        );
        EventLoopGroup group = new OioEventLoopGroup();
        try {
            /**
             * 创建ServerBootstrap
             */
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    /**
                     * 使用OioEventLoopGroup以允许阻塞模式
                     */
                    .channel(OioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    /**
                     * 指定ChannelInitializer,对于每个已接受的连接都调用它
                     */
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    /**
                                     * 添加一个ChannelInboundHandlerAdapter以拦截和处理事件
                                     */
                                    new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            ctx.writeAndFlush(buf.duplicate())
                                                    /**
                                                     * 将消息写到客户端,并添加ChannelFutureListener,
                                                     * 以便消息一被写完就关闭连接
                                                     */
                                                    .addListener(ChannelFutureListener.CLOSE);
                                        }
                                    }
                            );
                        }
                    });
            /**
             * 绑定服务器以接受连接
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
