package org.aidan.chapter1022;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;

public class HttpHeaderUtil {


    public static void setContentLength(HttpResponse response, long fileLength) {
        HttpHeaders.setContentLength(response, fileLength);
    }

    public static boolean isKeepAlive(FullHttpRequest request) {
        return HttpHeaders.isKeepAlive(request);
    }
}
