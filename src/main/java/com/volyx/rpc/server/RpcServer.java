package com.volyx.rpc.server;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.volyx.rpc.common.Handler;
import com.volyx.rpc.common.Response;
import com.volyx.rpc.common.ResponseFuture;
import io.netty.bootstrap.ServerBootstrap;
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
import com.volyx.rpc.handler.GetClientHandler;
import com.volyx.rpc.handler.GetHandlers;
import com.volyx.rpc.handler.HelloHandler;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final Map<String, ResponseFuture<Response>> futurePool = new ConcurrentHashMap<>();
    @Nonnull
    private final Map<String, Handler> handlers = new ConcurrentHashMap<>();
    public final static MetricRegistry registry = new MetricRegistry();

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

        final Slf4jReporter reporter = Slf4jReporter.forRegistry(registry)
                .outputTo(LoggerFactory.getLogger("[metrics]"))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(1, TimeUnit.MINUTES);

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        Handler handler = new HelloHandler();
        handlers.put(handler.getName(), handler);
        handler = new GetClientHandler();
        handlers.put(handler.getName(), handler);
        handler = new GetHandlers(handlers);
        handlers.put(handler.getName(), handler);

        RpcExecutor executor = new RpcExecutor(futurePool, handlers);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new RpcServerInitializer(sslCtx, executor));

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

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info("Stop server");
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        });
    }


    public static RpcServerBuilder builder() {
        return new RpcServerBuilder();
    }

    public void addHandler(@Nonnull Handler handler) {
        Objects.requireNonNull(handler);
        handlers.put(handler.getName(), handler);
    }


}
