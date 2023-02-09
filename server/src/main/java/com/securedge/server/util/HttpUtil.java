package com.securedge.server.util;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;

import static io.netty.handler.codec.rtsp.RtspHeaderNames.*;
import static io.netty.handler.codec.rtsp.RtspHeaderValues.CLOSE;
import static io.netty.handler.codec.rtsp.RtspResponseStatuses.OK;

public class HttpUtil {

    public static FullHttpResponse prepareResponse(String message) {
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK, Unpooled.wrappedBuffer(message.getBytes()));
        httpResponse.headers()
                .set(CONTENT_TYPE, "text/plain; charset=utf-8")
                .set(CONNECTION, CLOSE)
                .setInt(CONTENT_LENGTH, httpResponse.content().readableBytes());

        return httpResponse;
    }
}
