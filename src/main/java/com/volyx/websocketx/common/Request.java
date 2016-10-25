package com.volyx.websocketx.common;

import javax.annotation.Nonnull;

public class Request {
    @Nonnull
    private final String id;
    @Nonnull
    private final String method;
    @Nonnull
    private final String params;
    private final long startTime;

    public Request(String id, String method, String params) {
        this.id = id;
        this.method = method;
        this.params = params;
        this.startTime = System.currentTimeMillis();
    }
    @Nonnull
    public String getMethod() {
        return method;
    }
    @Nonnull
    public String getParams() {
        return method;
    }
    @Nonnull
    public String getId() {
        return id;
    }

    public long getStartTime() {
        return startTime;
    }
}
