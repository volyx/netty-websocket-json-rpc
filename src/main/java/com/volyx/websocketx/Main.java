package com.volyx.websocketx;

import com.volyx.websocketx.server.RpcServer;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        RpcServer rpcServer = null;
        try {
            rpcServer = RpcServer.builder().port(8080).build();

            Thread.currentThread().join();
        } finally {
            if (rpcServer != null) {
                rpcServer.shutdown();
            }
        }


    }

}
