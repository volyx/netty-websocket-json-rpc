package com.volyx.websocketx.server;

import com.volyx.websocketx.common.Handler;
import com.volyx.websocketx.common.Request;
import com.volyx.websocketx.common.RequestImpl;
import com.volyx.websocketx.common.Result;
import com.volyx.websocketx.repository.HandlerRepository;

import javax.annotation.Nonnull;

public class RequestExecutor {

    private static RequestExecutor instance;

    public static synchronized RequestExecutor getInstance() {
        if (instance == null) {
            instance = new RequestExecutor();
        }
        return instance;
    }

    @Nonnull
    public Result execute(@Nonnull Request req) throws HandlerNotFoundException {
        final Result result;
        if (req.isBatch()) {
            result = new Result<>("Batch");
        } else {
            RequestImpl request = (RequestImpl) req;
            final Handler handler = HandlerRepository.getInstance().get(request.getMethod());
            if (handler == null) {
                throw new HandlerNotFoundException("Handler " + request.getMethod() + " not found");
            }
            result = handler.execute(request);
        }

        return result;
    }
}
