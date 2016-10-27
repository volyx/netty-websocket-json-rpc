package com.volyx.rpc.common;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Response implements TextFrame {
    public static final int RESPONSE = 1;
    @Nonnull
    private final String id;
    @Nonnull
    private final String status;
    @Nullable
    private final String exception;
    @Nullable
    private final Result result;
    private final long duration;

    public Response(@Nonnull String id, @Nonnull long startTime, @Nonnull String status, @Nonnull Result result) {
        this.id = id;
        this.status = status;
        this.result = result;
        this.exception = null;
        this.duration = System.currentTimeMillis() - startTime;
    }
    public Response(@Nonnull String id, @Nonnull long startTime, @Nonnull String status, @Nonnull String exception) {
        this.id = id;
        this.status = status;
        this.exception = exception;
        this.result = null;
        this.duration = System.currentTimeMillis() - startTime;
    }
    @Nonnull
    public String getStatus() {
        return status;
    }
    @Nullable
    public String getException() {
        return exception;
    }
    @Nullable
    public Result getResult() {
        return result;
    }
    @Override
    @Nonnull
    public String getId() {
        return id;
    }

    @Override
    public int getType() {
        return RESPONSE;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isExceptional() {
        return exception != null;
    }
}
