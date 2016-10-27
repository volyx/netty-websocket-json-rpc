package com.volyx.rpc.handler;

import com.volyx.rpc.common.Handler;
import com.volyx.rpc.common.Request;
import com.volyx.rpc.common.Result;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetHandlers implements Handler {
    private Map<String, Handler> handlers;

    public GetHandlers(Map<String, Handler> handlers) {
        this.handlers = handlers;
    }

    @Nonnull
    @Override
    public Result execute(@Nonnull Request request) {
        List<String> names = handlers.values().stream().map(Handler::getName).collect(Collectors.toList());
        return new Result<>(names);
    }

    @Nonnull
    @Override
    public String getName() {
        return "gethandlers";
    }
}
