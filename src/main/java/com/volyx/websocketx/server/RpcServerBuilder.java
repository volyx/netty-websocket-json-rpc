package com.volyx.websocketx.server;

public class RpcServerBuilder {

    static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", SSL? "8443" : "8080"));
    private boolean enableSsl;
    private int port;

    public void port(int port) {
        this.port = port;
    }

    public void ssl(boolean enableSsl) {
        this.enableSsl = enableSsl;
    }

    public RpcServer build() {
        return new RpcServer(enableSsl, port);
    }
}
