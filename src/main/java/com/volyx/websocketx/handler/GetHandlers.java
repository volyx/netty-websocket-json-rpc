package com.volyx.websocketx.handler;

import com.volyx.websocketx.common.Handler;
import com.volyx.websocketx.common.Request;
import com.volyx.websocketx.common.RequestImpl;
import com.volyx.websocketx.common.Result;
import com.volyx.websocketx.repository.HandlerRepository;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class GetHandlers implements Handler {
    @Nonnull
    @Override
    public Result execute(@Nonnull Request request) {
        HandlerRepository handlerRepository = HandlerRepository.getInstance();
        List<String> handlers = handlerRepository.getAll().stream().map(Handler::getName).collect(Collectors.toList());
        return new Result<>(handlers);
    }

    @Nonnull
    @Override
    public String getName() {
        return "gethandlers";
    }
}
