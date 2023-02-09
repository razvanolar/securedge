package com.securedge.server.controller;

import com.securedge.server.config.SpringContextConfig;
import com.securedge.server.controller.handler.DataRequestHandler;
import com.securedge.server.controller.handler.HttpRouteHandler;
import com.securedge.server.controller.handler.UnknownRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiController {

    Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Value("${app.port}")
    private int PORT;

    public void start() {
        logger.info("Starting application server...");
        EventLoopGroup bossGroup = new NioEventLoopGroup(3);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();

                            pipeline.addLast(new HttpServerCodec())
                                    .addLast(new HttpServerExpectContinueHandler())
                                    .addLast(new HttpObjectAggregator(Integer.MAX_VALUE))
                                    .addLast(SpringContextConfig.getBean(HttpRouteHandler.class))
                                    .addLast(new UnknownRequestHandler())
                                    .addLast(SpringContextConfig.getBean(DataRequestHandler.class));
                        }
                    });

            ChannelFuture future = bootstrap.bind(PORT).sync();

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
