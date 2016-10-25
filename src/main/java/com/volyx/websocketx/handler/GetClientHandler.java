package com.volyx.websocketx.handler;

import com.volyx.websocketx.common.Handler;
import com.volyx.websocketx.common.Request;
import com.volyx.websocketx.common.Result;
import com.volyx.websocketx.server.ClientRepository;

import javax.annotation.Nonnull;

public class GetClientHandler implements Handler {
    @Nonnull
    @Override
    public Result execute(@Nonnull Request request) {
        return new Result<>(ClientRepository.getInstance().getClientInfos());
    }

    @Nonnull
    @Override
    public String getName() {
        return "getclient";
    }
}
