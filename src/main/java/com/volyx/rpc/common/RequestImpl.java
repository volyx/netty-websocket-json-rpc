package com.volyx.rpc.common;

import javax.annotation.Nonnull;

public class RequestImpl implements Request {
    public static final int REQUEST = 0;
    @Nonnull
    private final String id;
    @Nonnull
    private final String method;
    @Nonnull
    private final String params;
    private final long startTime = System.currentTimeMillis();

    public RequestImpl(@Nonnull String id, @Nonnull String method, @Nonnull String params) {
        this.id = id;
        this.method = method;
        this.params = params;
    }
    @Nonnull
    public String getMethod() {
        return method;
    }
    @Override
    @Nonnull
    public String getParams() {
        return method;
    }
    @Override
    @Nonnull
    public String getId() {
        return id;
    }

    @Override
    public int getType() {
        return REQUEST;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public boolean isBatch() {
        return false;
    }
}
