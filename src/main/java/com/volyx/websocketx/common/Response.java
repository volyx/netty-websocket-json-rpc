package com.volyx.websocketx.common;

public class Response {

    private String status;
    private String exception;
    private Result result;

    public Response(String status, Result result) {
        this.status = status;
        this.result = result;
    }

    public Response(String status, String exception) {
        this.status = status;
        this.exception = exception;
    }

    public String getStatus() {
        return status;
    }

    public String getException() {
        return exception;
    }

    public Result getResult() {
        return result;
    }
}
