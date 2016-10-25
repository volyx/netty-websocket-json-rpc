package com.volyx.websocketx.handler;

import com.volyx.websocketx.common.Handler;
import com.volyx.websocketx.common.Request;
import com.volyx.websocketx.common.Result;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.Date;

public class HelloHandler implements Handler {
    @Nonnull
    @Override
    public Result execute(@Nonnull Request request) {
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException ignored) {
        }

        Object[] objects = new Object[] {"123", 2L, new Integer(123), new Double(1.23), new BigDecimal(1.23), new Date()};

        return new Result<>(objects);
    }

    @Nonnull
    @Override
    public String getName() {
        return "hello";
    }
}
