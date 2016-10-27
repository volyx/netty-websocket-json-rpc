package com.volyx.rpc.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ResponseFuture<T> implements Future<T> {
    private static final Logger logger = LoggerFactory.getLogger(ResponseFuture.class);
    private final String id;
    private volatile T response;
    private volatile boolean cancelled = false;
    private final CountDownLatch responseLatch;
    private final Map<String, ResponseFuture<T>> futurePool;

    public ResponseFuture(String id, Map<String, ResponseFuture<T>> futurePool) {
        super();
        this.id = id;
        this.responseLatch = new CountDownLatch(1);
        this.futurePool = futurePool;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (this.isDone()) {
            return false;

        } else {
            this.responseLatch.countDown();
            this.cancelled = true;
            this.futurePool.remove(this.getId());

            return false == this.isDone();
        }
    }
    public String getId() {
        return this.id;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public boolean isDone() {
        return this.responseLatch.getCount() == 0;
    }

    @Override
    public T get() throws InterruptedException {
        try {
            this.responseLatch.await();

        } catch (InterruptedException e) {
            this.futurePool.remove(this.getId());
            throw e;
        }

        return this.response;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException {
        try {
            this.responseLatch.await(timeout, unit);

        } catch (InterruptedException e) {
            this.futurePool.remove(this.getId());
            throw e;
        }

        return this.response;
    }

    public void commit(T response) {
        this.response = response;
        this.responseLatch.countDown();

        this.futurePool.remove(this.getId());
    }
}
