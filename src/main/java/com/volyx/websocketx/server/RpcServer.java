package com.volyx.websocketx.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

public class RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    public RpcServer(boolean enableSsl, int port) {
        // Configure SSL.
        SslContext sslCtx = null;
        if (enableSsl) {
            try {
                final SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            } catch (CertificateException | SSLException e) {
                e.printStackTrace();
            }
        } else {
            sslCtx = null;
        }

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new WebSocketServerInitializer(sslCtx));

            ChannelFuture future = b.bind(port).awaitUninterruptibly();

            if (future.isDone()) {
                if (future.isSuccess()) {
                    logger.info("Open your web browser and navigate to " +
                            (enableSsl? "https" : "http") + "://127.0.0.1:" + port + '/');
                } else {
                    logger.error("Failed to start server cause " + future.cause());
                    future.cause().printStackTrace();
                }
            }

        } catch (Exception e){
            logger.error("Failed to start server", e);
        }
    }

    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public static RpcServerBuilder builder() {
        return new RpcServerBuilder();
    }
}
