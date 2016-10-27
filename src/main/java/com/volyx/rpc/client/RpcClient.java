package com.volyx.rpc.client;


import com.google.gson.Gson;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.volyx.rpc.common.*;
import com.volyx.rpc.json.Json;
import com.volyx.rpc.server.RpcExecutor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.SSLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

import static com.volyx.rpc.server.RpcExecutor.isRequest;
import static com.volyx.rpc.server.RpcServerInitializer.WEBSOCKET_PATH;

public class RpcClient {
    private static Logger logger = LoggerFactory.getLogger(RpcClient.class);
    @Nonnull
    private Channel channel;
    @Nonnull
    private Gson gson = Json.getInstance();
    private final Map<String, ResponseFuture<Response>> futurePool = new ConcurrentHashMap<>();
    @Nonnull
    private final Map<String, Handler> handlers = new ConcurrentHashMap<>();

    RpcClient(String host, int port, boolean ssl) {

        final SslContext sslCtx = getSslContext(ssl);

        final EventLoopGroup group = new NioEventLoopGroup();
        final String url = (ssl) ? "wss" : "ws" + "://" + host + ":" + port + WEBSOCKET_PATH;
        final URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            logger.error("Failed to create URI", e);
            throw new RuntimeException(e);
        }

        final Bootstrap b = new Bootstrap();
        final WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders());
        WebSocketClientProtocolHandler handler = new WebSocketClientProtocolHandler(handshaker);
        CompletableFuture<Boolean> clientFuture = new CompletableFuture<>();
        RpcExecutor executor = new RpcExecutor(futurePool, handlers);
        b.group(group)
                .remoteAddress(uri.getHost(), port)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        if (sslCtx != null) {
                            p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
                        }

                        p.addLast(
                                new HttpClientCodec(),
                                new HttpObjectAggregator(8192),
                                handler,
                                new SimpleChannelInboundHandler<WebSocketFrame>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
                                        if (!(frame instanceof TextWebSocketFrame)) {
                                            throw new UnsupportedOperationException("unsupported frame type: " + frame.getClass().getName());
                                        }
                                        final String stringFrame = ((TextWebSocketFrame) frame).text();
                                        logger.debug("{} received {}", ctx.channel(), stringFrame);
                                        final TextFrame textFrame = gson.fromJson(stringFrame, TextFrame.class);
                                        TextFrame response = executor.execute(textFrame);
                                        if (isRequest(textFrame)) {
                                            ctx.channel().writeAndFlush(new TextWebSocketFrame(gson.toJson(response)));
                                        }
                                    }
                                    @Override
                                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                        super.userEventTriggered(ctx, evt);
                                        if (evt.equals(WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE)) {
                                            logger.info(evt.toString());
                                            clientFuture.complete(true);
                                        }
                                    }
                                });
                    }
                });
        ChannelFuture future = b.connect().awaitUninterruptibly();
        if (future.isDone()) {
            if (future.isSuccess()) {
                this.channel = future.channel();
                logger.info("Client connect to {}", uri);
            } else {
                logger.error("Failed to connect to " + uri, future.cause());
                throw new RuntimeException();
            }
        } else {
            logger.error("Failed to connect to {}:{}", uri);
            throw new RuntimeException();
        }

        try {
            clientFuture.get(10L, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error("Failed to handshake to " + uri, e);
            throw new RuntimeException();
        }
        if (!clientFuture.isDone()) {
            logger.error("Failed to handshake to {}:{}", uri);
            throw new RuntimeException();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Client shutdown");
            group.shutdownGracefully();
        }));
    }

    @Nullable
    private SslContext getSslContext(boolean ssl) {
        if (ssl) {
            try {
                return SslContextBuilder.forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            } catch (SSLException e) {
                logger.error("Filed to create ssl context", e);

            }
        }
        return null;
    }


    public static RpcClientBuilder builder() {
        return new RpcClientBuilder();
    }

    public Result send(String method, String param) {
        // get a unique id for each request
        String id = UUID.randomUUID().toString();
        logger.debug("Start to send a request. id: {}", id);

        Request request = new RequestImpl(id, method, param);
        String requestJson = gson.toJson(request, Request.class);
        ResponseFuture<Response> future = new ResponseFuture<>(id, this.futurePool);
        this.futurePool.put(id, future);
        ChannelFuture channelFuture = this.channel.writeAndFlush(new TextWebSocketFrame(requestJson));
        channelFuture.addListener((ChannelFutureListener) f -> {
            if (f.isSuccess()) {
                logger.debug("Finish to send a request to the remote server. id: {}", id);

            } else {
                Throwable cause = f.cause();
                logger.error("Fail to send a request to the remote server.", cause);
            }
        });
        Response response;
        try {
            response = future.get();
        } catch (Exception e) {
            logger.error("Fail to process request ", e);
            throw new RuntimeException();
        }

        if (future.isCancelled()) {
            logger.error("A response future is cancelled.");
        }

        // get a result from a real response
        Result result = response.getResult();
        if (response.isExceptional()) {
            throw new RuntimeException(response.getException());
        }

        return result;
    }

    public void addHandler(@Nonnull Handler handler) {
        this.handlers.put(handler.getName(), handler);
    }
}
