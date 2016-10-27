package com.volyx.rpc.common;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.volyx.rpc.common.RequestImpl.REQUEST;

public class BatchRequest implements Request {
    @Nonnull
    private final String id;
    private final long startTime = System.currentTimeMillis();
    private List<Request> requests = new ArrayList<>();
    private String params;

    public BatchRequest(@Nonnull String id) {
        this.id = id;
    }

    @Nonnull
    @Override
    public String getParams() {
        return params;
    }

    @Nonnull
    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public boolean isBatch() {
        return true;
    }

    public void add(@Nonnull Request request) {
        Objects.requireNonNull(request);
        this.requests.add(request);
    }
    @Override
    public int getType() {
        return REQUEST;
    }
    @Nonnull
    public List<Request> getRequests() {
        return requests;
    }
}
