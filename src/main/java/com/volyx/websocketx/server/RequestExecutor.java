package com.volyx.websocketx.server;

import com.volyx.websocketx.common.*;
import com.volyx.websocketx.repository.HandlerRepository;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

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
            BatchRequest batch = (BatchRequest) req;
            List<Result> results = new ArrayList<>();
            for (Request r : batch.getRequests()) {
                results.add(executeRequest((RequestImpl) r));
            }

            result = new Result<>(results);
        } else {
            RequestImpl request = (RequestImpl) req;
            result = executeRequest(request);
        }

        return result;
    }

    private Result executeRequest(RequestImpl request) throws HandlerNotFoundException {
        Result result;
        final Handler handler = HandlerRepository.getInstance().get(request.getMethod());
        if (handler == null) {
            throw new HandlerNotFoundException("Handler " + request.getMethod() + " not found");
        }
        result = handler.execute(request);
        return result;
    }
}
