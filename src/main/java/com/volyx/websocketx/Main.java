package com.volyx.websocketx;

import com.volyx.websocketx.common.Handler;
import com.volyx.websocketx.common.Request;
import com.volyx.websocketx.common.Result;
import com.volyx.websocketx.server.RpcServer;

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
