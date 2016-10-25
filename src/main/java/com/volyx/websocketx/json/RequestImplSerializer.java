package com.volyx.websocketx.json;

import com.google.gson.*;
import com.volyx.websocketx.common.RequestImpl;

import java.lang.reflect.Type;
import java.util.UUID;

public class RequestImplSerializer implements JsonSerializer<RequestImpl>, JsonDeserializer<RequestImpl> {
    @Override
    public JsonElement serialize(RequestImpl request, Type type, JsonSerializationContext jsonSerializationContext) {

        final JsonObject result = new JsonObject();
        RequestImpl requestImpl = (RequestImpl) request;
        result.addProperty("method", requestImpl.getMethod());
        result.addProperty("params", requestImpl.getParams());
        result.addProperty("id", request.getId());
        return result;
    }

    @Override
    public RequestImpl deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {

        final JsonObject jsonObject = jsonElement.getAsJsonObject();
        final String method = jsonObject.get("method").getAsString();
        final String params = jsonObject.get("params").getAsString();
        final String id = UUID.randomUUID().toString();
        return new RequestImpl(id, method, params);
    }
}
