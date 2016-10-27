package com.volyx.rpc.server;

import com.google.gson.Gson;
import com.volyx.rpc.common.*;
import com.volyx.rpc.json.*;
import com.volyx.rpc.repository.ClientRepository;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class RpcServerFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private static final Logger logger = LoggerFactory.getLogger(RpcServerFrameHandler.class);
    private Gson gson = Json.getInstance();
    private RpcExecutor executor;

    public RpcServerFrameHandler(@Nonnull RpcExecutor executor) {
        this.executor = executor;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final WebSocketFrame frame) {
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException("unsupported frame type: " + frame.getClass().getName());
        }
        final String stringFrame = ((TextWebSocketFrame) frame).text();
        logger.debug("{} received {}", ctx.channel(), stringFrame);
        final TextFrame textFrame = gson.fromJson(stringFrame, TextFrame.class);
        TextFrame response = executor.execute(textFrame);

        ctx.channel().writeAndFlush(new TextWebSocketFrame(gson.toJson(response)));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.info("Good bye client {}", ctx.channel().id());
        ClientRepository.getInstance().remove(ctx.channel().id());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof  WebSocketServerProtocolHandler.HandshakeComplete) {
            ClientRepository.getInstance().put(ctx.channel().id(), ctx.channel());
            logger.info("new client {}", ctx.channel().id());
        }
    }
}
