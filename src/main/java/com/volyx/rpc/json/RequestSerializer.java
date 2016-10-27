package com.volyx.rpc.json;


import com.google.gson.*;
import com.volyx.rpc.common.BatchRequest;
import com.volyx.rpc.common.Request;
import com.volyx.rpc.common.RequestImpl;

import java.lang.reflect.Type;

public class RequestSerializer implements JsonSerializer<Request>, JsonDeserializer<Request> {
    @Override
    public Request deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        if (jsonElement.isJsonArray()) {
            return ctx.deserialize(jsonElement, BatchRequest.class);
        }
        return ctx.deserialize(jsonElement, RequestImpl.class);
    }

    @Override
    public JsonElement serialize(Request request, Type type, JsonSerializationContext ctx) {
        if (request.isBatch()) {
            return ctx.serialize(request, BatchRequest.class);
        }
        return ctx.serialize(request, RequestImpl.class);
    }
}
