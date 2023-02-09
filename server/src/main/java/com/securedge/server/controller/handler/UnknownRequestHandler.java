package com.securedge.server.controller.handler;

import com.securedge.server.util.HttpUtil;
import io.netty.channel.*;

@ChannelHandler.Sharable
public class UnknownRequestHandler extends SimpleChannelInboundHandler<Integer> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Integer msg) throws Exception {
        ChannelFuture future = ctx.writeAndFlush(HttpUtil.prepareResponse("Unrecognized route\n\r"));
        future.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
