package org.aidan.chapter043;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

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
                            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            socketChannel.pipeline().addLast(new StringDecoder());
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

        private int counter;

        private byte[] req;

        public TimeCliendHandler() {
            req = ("query time order" + System.getProperty("line.separator")).getBytes();
        }

        /**
         * TCP链路建立成功后，Netty的NIO线程会调用此方法
         * <p>
         * 即上来就发送查询时间的指令给服务端
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ByteBuf message = null;
            for (int i = 0; i < 100; i++) {
                message = Unpooled.buffer(req.length);
                message.writeBytes(req);
                ctx.writeAndFlush(message);
            }
        }

        /**
         * 收到服务器消息时触发
         *
         * @param ctx
         * @param msg
         * @throws Exception
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            String body = (String) msg;
            System.out.println("Now is : " + body + "; ther counter is " + ++counter);
        }

        /**
         * 发生异常时触发，释放资源
         *
         * @param ctx
         * @param cause
         * @throws Exception
         */
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
