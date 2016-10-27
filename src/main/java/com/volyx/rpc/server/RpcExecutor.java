package com.volyx.rpc.server;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.volyx.rpc.common.*;
import com.volyx.rpc.json.Json;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.volyx.rpc.common.RequestImpl.REQUEST;

public class RpcExecutor {
    private static final Logger logger = LoggerFactory.getLogger(RpcExecutor.class);
    private Gson gson = Json.getInstance();
    @Nonnull
    private Map<String, ResponseFuture<Response>> futurePool;
    private Map<String, Handler> handlers;

    public RpcExecutor(Map<String, ResponseFuture<Response>> futurePool, Map<String, Handler> handlers) {
        this.futurePool = futurePool;
        this.handlers = handlers;
    }

    public static boolean isRequest(TextFrame textFrame) {
        return textFrame.getType() == REQUEST;
    }

    @Nonnull
    public TextFrame execute(@Nonnull TextFrame frame) {

        if (isRequest(frame)) {
            return getResponse((Request) frame);
        }
        //response
        Response response = (Response) frame;

        String id = response.getId();
        ResponseFuture<Response> future = futurePool.get(id);

        if (future != null) {
            future.commit(response);
            logger.debug("Receive a response and commit a result. id: {}, channel proxy: {}", id, this);
        } else {
            throw new RuntimeException("Fail to find any matching future of response.");
        }
        return response;

    }

    private TextFrame getResponse(@Nonnull Request request) {

        Result result;
        Response response;
        try {
            if (request.isBatch()) {
                BatchRequest batch = (BatchRequest) request;
                List<Result> results = new ArrayList<>();
                for (Request r : batch.getRequests()) {
                    results.add(executeRequest((RequestImpl) r));
                }

                result = new Result<>(results);
            } else {
                RequestImpl requestImpl = (RequestImpl) request;
                result = executeRequest(requestImpl);
            }

            response = new Response(request.getId(), request.getStartTime(), "OK", result);
        } catch (HandlerNotFoundException e) {
            logger.error(e.getMessage(), e);
            response = new Response(request.getId(), request.getStartTime(), "ERROR", e.getMessage());
        }
        return response;
    }

    private Result executeRequest(@Nonnull RequestImpl request) throws HandlerNotFoundException {
        Result result;
        final Handler handler = handlers.get(request.getMethod());
        if (handler == null) {
            throw new HandlerNotFoundException("Handler " + request.getMethod() + " not found");
        }
        result = handler.execute(request);
        return result;
    }
}
