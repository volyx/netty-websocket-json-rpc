package com.volyx.rpc.client;


import com.volyx.rpc.server.RpcServerBuilder;

public class RpcClientBuilder {

    private int port;
    private boolean enableSsl;
    private String host = "127.0.0.1";

    RpcClientBuilder(){}

    public RpcClientBuilder port(int port) {
        this.port = port;
        return this;
    }

    public RpcClientBuilder ssl(boolean enableSsl) {
        this.enableSsl = enableSsl;
        return this;
    }

    public RpcClient build() {
        return new RpcClient(host, port, enableSsl);
    }
}
