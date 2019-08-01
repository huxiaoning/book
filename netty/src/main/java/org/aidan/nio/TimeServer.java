package org.aidan.nio;

public class TimeServer {
    public static void main(String[] args) {
        int port = 8080;
        MultiplexerTimeServer multiplexerTimeServer = new MultiplexerTimeServer(port);
        new Thread(multiplexerTimeServer, "NIO-TimeServer-001").start();
    }
}
