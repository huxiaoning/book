package org.aidan.chapter042;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Date;

public class TimeServer {
    public void bind(int port) throws Exception {

        /**
         * 配置服务端的NIO线程组
         *
         * Reaactor线程组
         */
        // 用于处理服务端接受客户端的连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 用于处理 SocketChannel 的网络 I/O
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            // 用于启动 NIO 服务端的辅助启动类实例，降低服务端的开发复杂度
            ServerBootstrap b = new ServerBootstrap();

            // 配置线程组
            b.group(bossGroup, workGroup)
                    // 配置创建的 channel 为 NioServerSocketChannel 类的实例
                    .channel(NioServerSocketChannel.class)
                    // NioServerSocketChannel 的 TCP 参数 backlog = 1024
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    // 绑定IO事件处理类 ChannelHandler实例,其作用类似 Reactor 模式中的Handler类,主要用于处理网络 I/O 事件。如：记录日志、消息编解码
                    .childHandler(new ChildChannelHandler());

            // 绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();

            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            // 处理网络 I/O 事件。如：记录日志、消息编解码
            socketChannel.pipeline().addLast(new TimeServerHandler());
        }
    }

    private class TimeServerHandler extends ChannelHandlerAdapter {

        private int counter;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);
            String body = new String(req, "UTF-8").substring(0, req.length - System.getProperty("line.separator").length());
            System.out.println("The time server receive order:" + body + "; the count is " + ++counter);
            String currentTime = "query time order".equalsIgnoreCase(body) ? new Date().toString() : "bad order";
            ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
            ctx.writeAndFlush(resp);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }
    }

    public static void main(String[] args) {
        int port = 8080;
        try {
            new TimeServer().bind(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
