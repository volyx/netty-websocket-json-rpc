package com.volyx.rpc.handler;

import com.volyx.rpc.common.Handler;
import com.volyx.rpc.common.Request;
import com.volyx.rpc.common.Result;
import com.volyx.rpc.repository.ClientRepository;

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
