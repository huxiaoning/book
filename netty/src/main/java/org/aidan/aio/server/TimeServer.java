package org.aidan.aio.server;

public class TimeServer {
    public static void main(String[] args) {
        int port = 8080;
        AsyncTimeServerHandler asyncTimeServerHandler = new AsyncTimeServerHandler(port);
        new Thread(asyncTimeServerHandler, "AIO-AsyncTimeServerHandler-001").start();
    }
}
