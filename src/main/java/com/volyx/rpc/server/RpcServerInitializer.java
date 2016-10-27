package com.volyx.rpc.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;

public class RpcServerInitializer extends ChannelInitializer<SocketChannel> {

    public static final String WEBSOCKET_PATH = "/websocket";

    private final SslContext sslCtx;
    private RpcExecutor executor;

    RpcServerInitializer(SslContext sslCtx, RpcExecutor executor) {
        this.sslCtx = sslCtx;
        this.executor = executor;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));
        pipeline.addLast(new IndexPageHandler(WEBSOCKET_PATH));
        pipeline.addLast(new RpcServerFrameHandler(executor));
    }
}
