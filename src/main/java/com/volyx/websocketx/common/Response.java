package com.volyx.websocketx.common;

import com.sun.istack.internal.Nullable;

import javax.annotation.Nonnull;
import java.util.Date;

public class Response {
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
    @Nonnull
    public String getId() {
        return id;
    }

    public long getDuration() {
        return duration;
    }
}
