package org.aidan.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TimeClient {

    public void connect(String host, int port) throws Exception {
        // 配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 创建客户端辅助启动类实例
            Bootstrap b = new Bootstrap();
            // 配置客户端NIO线程组
            b.group(group)
                    // 配置创建的 channel 为 NioSocketChannel 类的实例
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    // 配置用于处理网络事件的Handler
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 处理网络 I/O 事件。如：记录日志、消息编解码
                            socketChannel.pipeline().addLast(new TimeCliendHandler());
                        }
                    });
            // 发起异步连接操作，同步等待成功
            ChannelFuture f = b.connect(host, port).sync();
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放线程池资源
            group.shutdownGracefully();
        }
    }

    private class TimeCliendHandler extends ChannelHandlerAdapter {
        private final ByteBuf firstMessage;

        public TimeCliendHandler() {
            byte[] req = "query time order".getBytes();
            this.firstMessage = Unpooled.buffer(req.length);
            // write是给ByteBuf赋值
            this.firstMessage.writeBytes(req);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(firstMessage);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            byte[] req = new byte[buf.readableBytes()];
            // read是给byte[]赋值
            buf.readBytes(req);
            String body = new String(req, "UTF-8");
            System.out.println("Now is : " + body);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            // 释放资源
            ctx.close();
        }
    }

    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 8080;
        new TimeClient().connect(host, port);
    }
}
