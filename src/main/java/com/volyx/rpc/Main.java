package com.volyx.rpc;

import com.volyx.rpc.common.Handler;
import com.volyx.rpc.common.Request;
import com.volyx.rpc.common.Result;
import com.volyx.rpc.server.RpcServer;

import javax.annotation.Nonnull;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        RpcServer rpcServer = RpcServer.builder().port(8080).build();

        rpcServer.addHandler(new Handler() {
            @Nonnull
            @Override
            public Result execute(@Nonnull Request request) {
                return new Result<>(new Date());
            }

            @Nonnull
            @Override
            public String getName() {
                return "currentdate";
            }
        });




    }

}
