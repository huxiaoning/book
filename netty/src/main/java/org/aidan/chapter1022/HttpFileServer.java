package org.aidan.chapter1022;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpFileServer {

    private static final String DEFAULT_URL = "/netty/src/main/java";

    public void run(final int port, final String url) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // HTTP请求消息解码器
                            ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                            // HttpObjectAggregator 解码器：将多个消息转换为单一的 FullHttpRequest 或 FullHttpResponse
                            // 原因：HTTP解码器在每个HTTP消息中会生成多个消息对象
                            // 1 HttpRequest/HttpResponse
                            // 2 HttpContent
                            // 3 LastHttpContent
                            ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                            // HTTP响应编码器
                            ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                            // Chunked Handler : 支持异步发送大的码流（如：大文件的传输），但不占用过多内存，防止Java内存溢出
                            ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                            ch.pipeline().addLast("fileServerHandler", new HttpFileServerHandler(url));

                        }
                    });
            ChannelFuture f = b.bind(port).sync();
            System.out.println("HTTP 文件目录服务器启动成功！");
            System.out.println("网址是：http://127.0.0.1:" + port + url);
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    public static void main(String[] args) throws Exception {
        int port = 8080;
        String url = DEFAULT_URL;
        new HttpFileServer().run(port, url);
    }
}

