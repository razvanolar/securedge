package com.securedge.server.controller.handler;

import com.securedge.server.controller.ApiController;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class HttpRouteHandler extends SimpleChannelInboundHandler<Object> {

    Logger logger = LoggerFactory.getLogger(HttpRouteHandler.class);

    @Value("${app.root}")
    private String ROOT;

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, Object msg) throws Exception {
        logger.info("Handle http request");
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            if (request.uri().startsWith(ROOT)) {
                logger.info("Delegating http request to DataRequestHandler");
                context.fireChannelRead(request);
            } else {
                logger.info("Http request not recognized");
                context.fireChannelRead(404);
            }
        } else {
            logger.info("Http request not recognized");
            context.fireChannelRead(404);
        }
    }
}
