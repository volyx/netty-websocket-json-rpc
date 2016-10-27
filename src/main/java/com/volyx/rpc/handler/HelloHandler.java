package com.volyx.rpc.handler;

import com.volyx.rpc.common.Handler;
import com.volyx.rpc.common.Request;
import com.volyx.rpc.common.Result;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.Date;

public class HelloHandler implements Handler {
    @Nonnull
    @Override
    public Result execute(@Nonnull Request request) {
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException ignored) {
        }

        String params = request.getParams();

        Object[] objects = new Object[] {params, 2L, new Integer(123), new Double(1.23), new BigDecimal(1.23), new Date()};

        return new Result<>(objects);
    }

    @Nonnull
    @Override
    public String getName() {
        return "hello";
    }
}
