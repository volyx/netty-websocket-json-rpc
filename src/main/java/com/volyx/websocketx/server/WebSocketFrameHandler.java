package com.volyx.websocketx.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.volyx.websocketx.common.*;
import com.volyx.websocketx.repository.ClientRepository;
import com.volyx.websocketx.repository.HandlerRepository;
import com.volyx.websocketx.json.ClientInfoSerializer;
import com.volyx.websocketx.json.RequestSerializer;
import com.volyx.websocketx.json.ResponseSerializer;
import com.volyx.websocketx.json.ResultSerializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketFrameHandler.class);
    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Request.class, new RequestSerializer())
            .registerTypeAdapter(Response.class, new ResponseSerializer())
            .registerTypeAdapter(Result.class, new ResultSerializer())
            .registerTypeAdapter(ClientRepository.ClientInfo.class, new ClientInfoSerializer())
            .create();

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final WebSocketFrame frame) throws Exception {
        if (!(frame instanceof TextWebSocketFrame)) {
            String message = "unsupported frame type: " + frame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
        final String stringFrame = ((TextWebSocketFrame) frame).text();
        logger.info("{} received {}", ctx.channel(), stringFrame);
        final Request request = gson.fromJson(stringFrame, Request.class);

        final Handler handler = HandlerRepository.getInstance().get(request.getMethod());
        final Response response;
        if (handler == null) {
            response = new Response("ERROR", "Handler " + request.getMethod() + " not found");
            ctx.channel().writeAndFlush(new TextWebSocketFrame(gson.toJson(response)));
            return;
        }

        final Result result = handler.execute(request);
        response = new Response("OK", result);
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
