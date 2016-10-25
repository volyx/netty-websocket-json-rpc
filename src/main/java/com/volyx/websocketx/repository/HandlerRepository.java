package com.volyx.websocketx.repository;

import com.volyx.websocketx.common.Handler;
import com.volyx.websocketx.handler.GetClientHandler;
import com.volyx.websocketx.handler.GetHandlers;
import com.volyx.websocketx.handler.HelloHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class HandlerRepository {
    @Nonnull
    private Map<String, Handler> handlers = new ConcurrentHashMap<>();

    @Nonnull
    private static HandlerRepository instance;

    @Nonnull
    public synchronized static HandlerRepository getInstance() {
        if (instance == null) {
            instance = new HandlerRepository();
            instance.add(new HelloHandler());
            instance.add(new GetClientHandler());
            instance.add(new GetHandlers());
        }
        return instance;
    }

    public void add(@Nonnull Handler handler) {
        Objects.requireNonNull(handler);
        if (handlers.containsKey(handler.getName())) {
            throw new RuntimeException("Handler with name " + handler.getName() + " already exists");
        }
        handlers.put(handler.getName(), handler);
    }

    @Nullable
    public Handler get(@Nonnull String method) {
        Objects.requireNonNull(method);
        return handlers.get(method);
    }
    @Nonnull
    public List<Handler> getAll() {
        return new ArrayList<>(handlers.values());
    }
}
