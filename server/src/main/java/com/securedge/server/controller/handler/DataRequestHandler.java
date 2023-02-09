package com.securedge.server.controller.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.securedge.server.handler.AccountHandler;
import com.securedge.server.handler.UserHandler;
import com.securedge.server.response.GenericResponse;
import com.securedge.server.util.HttpUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@ChannelHandler.Sharable
public class DataRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    Logger logger = LoggerFactory.getLogger(DataRequestHandler.class);

    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${app.users}")
    private String USERS_URI;

    @Value("${app.accounts}")
    private String ACCOUNTS_URI;

    private final UserHandler userHandler;

    private final AccountHandler accountHandler;

    @Autowired
    public DataRequestHandler(UserHandler userHandler, AccountHandler accountHandler) {
        this.userHandler = userHandler;
        this.accountHandler = accountHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, FullHttpRequest request) throws Exception {
        GenericResponse response = handlerRequest(request);
        String responseString = mapper.writeValueAsString(response);

        ChannelFuture future = context.writeAndFlush(HttpUtil.prepareResponse(responseString));
        future.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    protected GenericResponse handlerRequest(FullHttpRequest request) throws JsonProcessingException, InterruptedException {
        String uri = request.uri();
        logger.info("Request received for: " + uri);
        if (uri.startsWith(USERS_URI)) {
            logger.info("Delegating request to UserHandler");
            return userHandler.handle(request);
        } else if (uri.startsWith(ACCOUNTS_URI)) {
            logger.info("Delegating request to AccountHandler");
            return accountHandler.handle(request);
        }
        return GenericResponse.ok();
    }

    private String computeResponse(Object object) throws JsonProcessingException {
        if (Objects.isNull(object)) {
            return "{}";
        }
        return mapper.writeValueAsString(object);
    }

    private int getIdFromRoute(String route) {
        int index = route.lastIndexOf("/");
        return Integer.parseInt(route.substring(index + 1));
    }
}